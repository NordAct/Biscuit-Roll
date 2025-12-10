package nordmods.testmod.common;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import nordmods.biscuit_roll.common.animation.BRAnimatedObject;
import nordmods.biscuit_roll.common.animation.BRAnimationController;

import java.util.Collection;
import java.util.List;

public class Drone extends Mob implements BRAnimatedObject {
    private final BRAnimationController controller1 = new BRAnimationController(this);;
    private final BRAnimationController controller2 = new BRAnimationController(this);;
    public Drone(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public Collection<BRAnimationController> getAnimationControllers() {
        return List.of(controller1, controller2);
    }

    @Override
    public boolean isClient() {
        return level().isClientSide();
    }

    public void tick() {
        super.tick();
        if (isClient()) {
            controller1.playAnimation("idle");
            controller2.playAnimation("default");
        }
    }
}
