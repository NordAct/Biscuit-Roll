package nordmods.testmod.common;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import nordmods.biscuit_roll.common.animation.BRAnimatedObject;
import nordmods.biscuit_roll.common.animation.BRAnimationController;

import java.util.Collection;
import java.util.List;

public class WaterDragon extends Mob implements BRAnimatedObject {
    private final BRAnimationController controller = new BRAnimationController(this);

    public WaterDragon(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void addMolangVariables(Context context) {
        context.addQuery("anim_time", tickCount / 20f);
    }

    @Override
    public Collection<BRAnimationController> getAnimationControllers() {
        return List.of(controller);
    }

    @Override
    public boolean isClient() {
        return level().isClientSide();
    }

    @Override
    public void tick() {
        super.tick();
        if (isClient()) controller.playAnimation("pose");
    }
}
