package nordmods.testmod.common;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;

public class Drone extends Mob {
    public Drone(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }
}
