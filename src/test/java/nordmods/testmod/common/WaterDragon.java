package nordmods.testmod.common;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import nordmods.biscuit_roll.common.animation.BRAnimatedObject;
import nordmods.biscuit_roll.common.animation.BRAnimationController;
import nordmods.biscuit_roll.common.animation.EntityAnimationController;

import java.util.Collection;
import java.util.List;

public class WaterDragon extends Mob implements BRAnimatedObject {
    private final BRAnimationController<WaterDragon> controller = new EntityAnimationController<>(this, false);

    public WaterDragon(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public Collection<BRAnimationController<?>> getAnimationControllers() {
        return List.of(controller);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide()) {
            controller.playAnimation("pose");
        }
    }
}
