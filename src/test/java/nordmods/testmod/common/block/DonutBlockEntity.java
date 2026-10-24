package nordmods.testmod.common.block;

import gg.moonflower.pinwheel.api.animation.AnimationData;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import nordmods.biscuit_roll.common.animation.BRBlockEntityWithAnimationProcessor;
import nordmods.biscuit_roll.common.animation.controller.BRAnimationController;
import nordmods.biscuit_roll.common.animation.controller.SideAwareAnimationController;
import nordmods.biscuit_roll.common.animation.processor.AnimationProcessor;
import nordmods.biscuit_roll.common.model.BRModel;
import nordmods.biscuit_roll.common.model.BRModelProvider;
import nordmods.biscuit_roll.common.state.BRState;
import nordmods.testmod.TestMod;

import java.util.LinkedList;
import java.util.List;

public class DonutBlockEntity extends BlockEntity implements BRBlockEntityWithAnimationProcessor<DonutBlockEntity> {
    public int ticks;
    public final AnimationProcessor<DonutBlockEntity> animationProcessor = new AnimationProcessor<>(this) {
        @Override
        public BRModelProvider getModelProvider() {
            return MODEL_PROVIDER;
        }
    };
    public final BRAnimationController controller = new SideAwareAnimationController(true, animationProcessor::isClient) {
        @Override
        protected void onSoundEffect(AnimationData.SoundEffect soundEffect, BRModel model, BRState state) {}

        @Override
        protected void onParticleEffect(AnimationData.ParticleEffect particleEffect, BRModel model, BRState state) {}

        @Override
        protected void onTimelineEffect(AnimationData.TimelineEffect timelineEffect, BRModel model, BRState state) {}
    };
    public final LinkedList<BRAnimationController> controllers = new LinkedList<>(List.of(controller));
    public static final BRModelProvider MODEL_PROVIDER = new BRModelProvider() {
        private static final Identifier MODEL = Identifier.fromNamespaceAndPath(TestMod.MOD_ID, "biscuit_roll/models/donut.geo.json");
        private static final Identifier ANIMATION = Identifier.fromNamespaceAndPath(TestMod.MOD_ID, "biscuit_roll/animations/donut.animation.json");
        @Override
        public Identifier getModelId(BRState state) {
            return MODEL;
        }

        @Override
        public Identifier getAnimationId(BRState state) {
            return ANIMATION;
        }
    };
    public DonutBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(TestMod.DONUT_BLOCK_ENTITY, blockPos, blockState);
    }

    @Override
    public LinkedList<BRAnimationController> getAnimationControllers() {
        return controllers;
    }

    @Override
    public AnimationProcessor<DonutBlockEntity> getAnimationProcessor() {
        return animationProcessor;
    }

    @Override
    public float getAnimationTime() {
        return ticks / 20f;
    }
}
