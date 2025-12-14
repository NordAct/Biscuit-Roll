package nordmods.biscuit_roll.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import gg.moonflower.molangcompiler.api.MolangEnvironmentBuilder;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.*;
import nordmods.biscuit_roll.client.state.ClientStateDataTypes;
import nordmods.biscuit_roll.common.animation.BRAnimatedObject;
import nordmods.biscuit_roll.common.model.BRModelProvider;
import nordmods.biscuit_roll.common.state.StateDataTypes;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class BREntityRenderer<E extends Entity & BRAnimatedObject, S extends EntityRenderState> extends EntityRenderer<E, S> implements BRRenderer<S> {
    private final BRModelProvider modelProvider;
    private final LivingRenderStateGetter<LivingEntity, LivingEntityRenderState, EntityModel<LivingEntityRenderState>> livingEntityStateGetter;
    private final MobRenderStateGetter<Mob, LivingEntityRenderState, EntityModel<LivingEntityRenderState>> mobRenderStateGetter;
    private final List<BRRenderLayer> renderLayers = new ArrayList<>();

    protected BREntityRenderer(EntityRendererProvider.Context context, BRModelProvider modelProvider, float deathFlipDegrees) {
        super(context);
        this.modelProvider = modelProvider;
        this.livingEntityStateGetter = new LivingRenderStateGetter<>(context, deathFlipDegrees);
        this.mobRenderStateGetter = new MobRenderStateGetter<>(context);
    }

    protected BREntityRenderer(EntityRendererProvider.Context context, BRModelProvider modelProvider) {
        this(context, modelProvider, 90f);
    }

    @Override
    public Collection<BRRenderLayer> getRenderLayers() {
        return renderLayers;
    }

    @Override
    public void beforeSubmit(S state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        float scale = state.getStateDataOptional(StateDataTypes.SCALE).orElse(1f);
        poseStack.scale(scale);
        if (state instanceof LivingEntityRenderState livingState) {
            livingEntityStateGetter.rotate(livingState, poseStack, state.getStateDataOptional(StateDataTypes.BODY_YAW).orElse(0f), scale);
        }
        poseStack.scale(-1, -1, 1);
    }

    @Override
    public void submit(S state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        submitBRModel(state, poseStack, submitNodeCollector, cameraRenderState);
        super.submit(state, poseStack, submitNodeCollector, cameraRenderState);
    }

    @Override
    public BRModelProvider getModelProvider() {
        return modelProvider;
    }

    public void updateControllerVariables(MolangEnvironmentBuilder<?> builder, E entity, float tickDelta) { //todo add other queries
        builder.setQuery("anim_time", (entity.tickCount + tickDelta) / 20f);
        builder.setQuery("is_swimming", entity.isSwimming() ? 1 : 0);

        if (entity instanceof LivingEntity living) {
            builder.setQuery("swim_amount", living.getSwimAmount(tickDelta));
            builder.setQuery("can_climb", living.onClimbable() ? 1 : 0);
            builder.setQuery("can_fly", living.canGlide() ? 1 : 0);
            builder.setQuery("blocking", living.isBlocking() ? 1 : 0);
            if (living instanceof Mob mob) {
                builder.setQuery("can_damage_nearby_mobs", !mob.level().getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(5), target -> ((Mob) entity).canAttack(target)).isEmpty() ? 1 : 0);
            }
        }
        if (entity instanceof PlayerRideableJumping jumping) builder.setQuery("can_power_jump", jumping.canJump() ? 1 : 0);
    }

    @Override
    public void extractRenderState(E entity, S state, float tickDelta) {
        entity.getAnimationControllers().forEach(controller -> updateControllerVariables(controller.getEnvironment().edit(), entity, tickDelta));
        super.extractRenderState(entity, state, tickDelta);
        state.setStateData(ClientStateDataTypes.INVISIBLE, state.isInvisible);
        if (state instanceof LivingEntityRenderState livingState && entity instanceof LivingEntity livingEntity) {
            if (entity instanceof Mob mob) mobRenderStateGetter.fillRenderState(mob, livingState, tickDelta);
            else livingEntityStateGetter.fillRenderState(livingEntity, livingState, tickDelta);
            state.setStateData(ClientStateDataTypes.OVERLAY_TEXTURE, LivingEntityRenderer.getOverlayCoords(livingState, livingEntityStateGetter.getWhiteOverlayProgress(livingState)));
            state.setStateData(StateDataTypes.BODY_YAW, livingState.bodyRot);
            state.setStateData(StateDataTypes.SCALE, livingState.scale * livingState.ageScale);
            state.setStateData(ClientStateDataTypes.INVISIBLE, livingState.isInvisibleToPlayer);
        }
        state.setStateData(ClientStateDataTypes.OUTLINE_COLOR, state.outlineColor);
        state.setStateData(ClientStateDataTypes.LIGHT, state.lightCoords);

        state.setStateData(StateDataTypes.TICK_DELTA, tickDelta);
        state.setStateData(StateDataTypes.CONTROLLERS, entity.getAnimationControllers());
        state.setStateData(StateDataTypes.MODEL_PROVIDER, getModelProvider());
    }


    @ApiStatus.Internal
    private record LivingRenderStateGetter<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>>(LivingEntityRenderer<T, S, M> renderer) {
        private LivingRenderStateGetter(EntityRendererProvider.Context context, float deathFlipDegrees) {
            this(new LivingEntityRenderer<>(context, null, 0) {
                @Override
                public Identifier getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
                    return null;
                }

                @Override
                public S createRenderState() {
                    return null;
                }

                @Override
                protected float getFlipDegrees() {
                    return deathFlipDegrees;
                }
            });
        }

        public void fillRenderState(T entity, S state, float tickDelta) {
            renderer.extractRenderState(entity, state, tickDelta);
        }

        public float getWhiteOverlayProgress(S state) {
            return renderer.getWhiteOverlayProgress(state);
        }

        public void rotate(S livingEntityRenderState, PoseStack poseStack, float bodyYaw, float scale) {
            renderer.setupRotations(livingEntityRenderState, poseStack, bodyYaw, scale);
        }
    }

    @ApiStatus.Internal
    private record MobRenderStateGetter<T extends Mob, S extends LivingEntityRenderState, M extends EntityModel<? super S>>(MobRenderer<T, S, M> renderer) {
        private MobRenderStateGetter(EntityRendererProvider.Context context) {
            this(new MobRenderer<>(context, null, 0) {
                @Override
                public Identifier getTextureLocation(LivingEntityRenderState livingEntityRenderState) {
                    return null;
                }

                @Override
                public S createRenderState() {
                    return null;
                }
            });
        }

        public void fillRenderState(T entity, S state, float tickDelta) {
            renderer.extractRenderState(entity, state, tickDelta);
        }
    }
}
