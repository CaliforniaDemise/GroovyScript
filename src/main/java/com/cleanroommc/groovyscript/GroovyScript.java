package com.cleanroommc.groovyscript;

import com.cleanroommc.groovyscript.brackets.BracketHandlerManager;
import com.cleanroommc.groovyscript.command.GSCommand;
import com.cleanroommc.groovyscript.compat.vanilla.VanillaModule;
import com.cleanroommc.groovyscript.helper.JsonHelper;
import com.cleanroommc.groovyscript.network.NetworkHandler;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import com.cleanroommc.groovyscript.sandbox.GroovyDeobfuscationMapper;
import com.cleanroommc.groovyscript.sandbox.GroovyScriptSandbox;
import com.cleanroommc.groovyscript.sandbox.RunConfig;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

@Mod(modid = GroovyScript.ID, name = GroovyScript.NAME, version = GroovyScript.VERSION)
@Mod.EventBusSubscriber(modid = GroovyScript.ID)
public class GroovyScript {

    public static final String ID = "groovyscript";
    public static final String NAME = "GroovyScript";
    public static final String VERSION = "1.0.0";

    public static final Logger LOGGER = LogManager.getLogger(ID);

    private static File scriptPath;
    private static RunConfig runConfig;
    private static GroovyScriptSandbox sandbox;

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        NetworkHandler.init();
        GroovyDeobfuscationMapper.init();
        ReloadableRegistryManager.init();
        scriptPath = new File(Loader.instance().getConfigDir().toPath().getParent().toString() + File.separator + "groovy");
        try {
            sandbox = new GroovyScriptSandbox(getScriptFile().toURI().toURL());
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Error initializing sandbox!");
        }
        runConfig = createRunConfig();
        BracketHandlerManager.init();
        VanillaModule.initializeBinding();

        getSandbox().run("preInit");
    }

    @Mod.EventHandler
    public void onPostInit(FMLPostInitializationEvent event) {
        getSandbox().run("postInit");
    }

    @Mod.EventHandler
    public void onServerLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new GSCommand());
    }

    @NotNull
    public static String getScriptPath() {
        return getScriptFile().getPath();
    }

    @NotNull
    public static File getScriptFile() {
        if (scriptPath == null) {
            throw new IllegalStateException("GroovyScript is not yet loaded!");
        }
        return scriptPath;
    }

    @NotNull
    public static GroovyScriptSandbox getSandbox() {
        if (sandbox == null) {
            throw new IllegalStateException("GroovyScript is not yet loaded!");
        }
        return sandbox;
    }

    public static RunConfig getRunConfig() {
        return runConfig;
    }

    private static RunConfig createRunConfig() {
        File runConfigFile = new File(scriptPath.getPath() + File.separator + "runConfig.json");
        if (!Files.exists(runConfigFile.toPath())) {
            JsonObject json = RunConfig.createDefaultJson();
            JsonHelper.saveJson(runConfigFile, json);
            File main = new File(scriptPath.getPath() + File.separator + "postInit" + File.separator + "main.groovy");
            if (!Files.exists(main.toPath())) {
                try {
                    main.getParentFile().mkdirs();
                    Files.write(main.toPath(), "\nprintln('Hello World!')\n".getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return new RunConfig(json);
        }
        JsonElement element = JsonHelper.loadJson(runConfigFile);
        if (element == null) return new RunConfig(new JsonObject());
        if (!element.isJsonObject()) {
            LOGGER.error("runConfig.json must be a json object!");
            return new RunConfig(new JsonObject());
        }
        return new RunConfig(element.getAsJsonObject());
    }
}
