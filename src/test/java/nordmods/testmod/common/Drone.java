package nordmods.testmod.common;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import nordmods.biscuit_roll.common.animation.BRAnimatedObject;
import nordmods.biscuit_roll.common.animation.BRAnimationController;
import nordmods.biscuit_roll.common.animation.EntityAnimationController;

import java.util.Collection;
import java.util.List;

public class Drone extends Mob implements BRAnimatedObject {
    private final BRAnimationController<Drone> controller1 = new EntityAnimationController<>(this, false);
    private final BRAnimationController<Drone> controller2 = new EntityAnimationController<>(this, false);
    public Drone(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public Collection<BRAnimationController<?>> getAnimationControllers() {
        return List.of(controller1, controller2);
    }

    public void tick() {
        super.tick();
        if (level().isClientSide()) {
            controller1.playAnimation("idle");
            controller2.playAnimation("default");
        }
    }
}
