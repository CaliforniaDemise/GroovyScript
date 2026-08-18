package com.cleanroommc.groovyscript.compat.mods.immersivepetroleum;

import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;

public class ImmersivePetroleum extends GroovyPropertyContainer {

    public final Distillation distillation = new Distillation();
    public final Reservoir reservoir = new Reservoir();
    public final Lubricant lubricant = new Lubricant();
    public final MotorboatFuel motorboatFuel = new MotorboatFuel();
    public final PortableGenerator portableGenerator = new PortableGenerator();
}
