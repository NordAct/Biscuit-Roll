package nordmods.testmod.common.block;

import gg.moonflower.pinwheel.api.animation.AnimationData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import nordmods.biscuit_roll.common.animation.BRAnimatedObject;
import nordmods.biscuit_roll.common.animation.controller.BRAnimationController;
import nordmods.biscuit_roll.common.model.BRModel;
import nordmods.biscuit_roll.common.state.BRState;
import nordmods.testmod.TestMod;

import java.util.Collection;
import java.util.List;

public class DonutBlockEntity extends BlockEntity implements BRAnimatedObject {
    public int ticks;
    public final BRAnimationController controller = new BRAnimationController(true, true) {
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
    public final Collection<BRAnimationController> controllers = List.of(controller);
    public DonutBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(TestMod.DONUT_BLOCK_ENTITY, blockPos, blockState);
    }

    @Override
    public Collection<BRAnimationController> getAnimationControllers() {
        return controllers;
    }
}
