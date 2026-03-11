package nordmods.testmod.common.entity;

import gg.moonflower.pinwheel.api.animation.AnimationData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import nordmods.biscuit_roll.common.animation.BRAnimatedObject;
import nordmods.biscuit_roll.common.animation.controller.BRAnimationController;
import nordmods.biscuit_roll.common.model.BRModel;
import nordmods.biscuit_roll.common.state.BRState;

import java.util.Collection;
import java.util.List;

public class WaterDragon extends Mob implements BRAnimatedObject {
    private final BRAnimationController controller = new BRAnimationController(false) {
        @Override
        protected void onSoundEffect(AnimationData.SoundEffect soundEffect, BRModel model, BRState state) {

        }

        @Override
        protected void onParticleEffect(AnimationData.ParticleEffect particleEffect, BRModel model, BRState state) {

        }

        @Override
        protected void onTimelineEffect(AnimationData.TimelineEffect timelineEffect, BRModel model, BRState state) {

        }
    };

    public WaterDragon(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public Collection<BRAnimationController> getAnimationControllers() {
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
