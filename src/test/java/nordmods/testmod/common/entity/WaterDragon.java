package nordmods.testmod.common.entity;

import gg.moonflower.pinwheel.api.animation.AnimationData;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import nordmods.biscuit_roll.common.animation.BREntityWithAnimationProcessor;
import nordmods.biscuit_roll.common.animation.controller.BRAnimationController;
import nordmods.biscuit_roll.common.animation.controller.SideAwareAnimationController;
import nordmods.biscuit_roll.common.animation.processor.AnimationProcessor;
import nordmods.biscuit_roll.common.model.BRModel;
import nordmods.biscuit_roll.common.model.BRModelProvider;
import nordmods.biscuit_roll.common.state.BRState;
import nordmods.testmod.TestMod;
import org.jspecify.annotations.NonNull;

import java.util.LinkedList;
import java.util.List;

public class WaterDragon extends Mob implements BREntityWithAnimationProcessor<WaterDragon> {
    private int animatonOrdinal = 0;
    private final AnimationProcessor<WaterDragon> animationProcessor = new AnimationProcessor<>(this) {
        @Override
        public BRModelProvider getModelProvider() {
            return MODEL_PROVIDER;
        }
    };
    private final BRAnimationController controller = new SideAwareAnimationController(true, animationProcessor::isClient) {
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
    private final LinkedList<BRAnimationController> controllers = new LinkedList<>(List.of(controller));
    private final String[] animations = {"pose", "idle.land", "idle.water"};
    public static final BRModelProvider MODEL_PROVIDER = new BRModelProvider() {
        private static final Identifier MODEL = Identifier.fromNamespaceAndPath(TestMod.MOD_ID, "biscuit_roll/models/water_dragon.geo.json");
        private static final Identifier ANIMATION = Identifier.fromNamespaceAndPath(TestMod.MOD_ID, "biscuit_roll/animations/water_dragon.animation.json");
        @Override
        public Identifier getModelId(BRState state) {
            return MODEL;
        }

        @Override
        public Identifier getAnimationId(BRState state) {
            return ANIMATION;
        }
    };
    public WaterDragon(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void addAdditionalSaveData(@NonNull ValueOutput valueOutput) {
        super.addAdditionalSaveData(valueOutput);
        valueOutput.putInt("Animation", animatonOrdinal);
    }

    @Override
    protected void readAdditionalSaveData(@NonNull ValueInput valueInput) {
        super.readAdditionalSaveData(valueInput);
        animatonOrdinal = valueInput.getIntOr("Animation", 0);
    }

    @Override
    public LinkedList<BRAnimationController> getAnimationControllers() {
        return controllers;
    }

    @Override
    public AnimationProcessor<WaterDragon> getAnimationProcessor() {
        return animationProcessor;
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide()) controller.playAnimation(animations[animatonOrdinal]);
    }

    @Override
    protected @NonNull InteractionResult mobInteract(@NonNull Player player, @NonNull InteractionHand interactionHand) {
        if (player.isShiftKeyDown()) {
            animatonOrdinal = (animatonOrdinal + 1) % animations.length;
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, interactionHand);
    }
}
