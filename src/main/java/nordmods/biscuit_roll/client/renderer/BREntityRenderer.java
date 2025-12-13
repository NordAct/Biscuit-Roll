package nordmods.biscuit_roll.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.*;
import nordmods.biscuit_roll.client.internal.BRModelSubmitStorage;
import nordmods.biscuit_roll.client.state.ClientStateDataTypes;
import nordmods.biscuit_roll.common.animation.BRAnimatedObject;
import nordmods.biscuit_roll.common.animation.BRAnimationController;
import nordmods.biscuit_roll.common.model.BRModelProvider;
import nordmods.biscuit_roll.common.state.BRState;
import nordmods.biscuit_roll.common.state.StateDataTypes;
import org.jetbrains.annotations.ApiStatus;

public abstract class BREntityRenderer<E extends Entity & BRAnimatedObject, S extends EntityRenderState> extends EntityRenderer<E, S> implements BRRenderer<S>{
    private final BRModelProvider modelProvider;
    private final LivingRenderStateGetter<LivingEntity, LivingEntityRenderState, EntityModel<LivingEntityRenderState>> livingEntityStateGetter;
    private final MobRenderStateGetter<Mob, LivingEntityRenderState, EntityModel<LivingEntityRenderState>> mobRenderStateGetter;

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
    public void submit(S state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        poseStack.pushPose();
        beforeSubmit(state, poseStack, submitNodeCollector, cameraRenderState);
        Identifier texture = getTextureId(state);
        ((BRModelSubmitStorage)submitNodeCollector).biscuit_roll$submit(
                poseStack.last().copy(),
                getModel(state),
                state,
                this::getRenderType,
                texture
        );
        afterSubmit(state, poseStack, submitNodeCollector, cameraRenderState);
        poseStack.popPose();
        super.submit(state, poseStack, submitNodeCollector, cameraRenderState);
    }

    @Override
    public BRModelProvider getModelProvider() {
        return modelProvider;
    }

    public abstract RenderType getRenderType(BRState state, Identifier texture);

    public abstract Identifier getTextureId(BRState state);

    public void beforeSubmit(S state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        float scale = state.getStateDataOptional(StateDataTypes.SCALE).orElse(1f);
        poseStack.scale(scale);
        if (state instanceof LivingEntityRenderState livingState) {
            livingEntityStateGetter.rotate(livingState, poseStack, state.getStateDataOptional(StateDataTypes.BODY_YAW).orElse(0f), scale);
        }
        poseStack.scale(-1, -1, 1);
    }

    public void afterSubmit(S state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {

    }

    @Override
    public void extractRenderState(E entity, S state, float tickDelta) {
        entity.getAnimationControllers().forEach(controller -> updateControllerVariables(controller, entity, tickDelta));
        super.extractRenderState(entity, state, tickDelta);
        if (state instanceof LivingEntityRenderState livingState && entity instanceof LivingEntity livingEntity) {
            if (entity instanceof Mob mob) mobRenderStateGetter.fillRenderState(mob, livingState, tickDelta);
            else livingEntityStateGetter.fillRenderState(livingEntity, livingState, tickDelta);
            state.setStateData(ClientStateDataTypes.OVERLAY_TEXTURE, LivingEntityRenderer.getOverlayCoords(livingState, livingEntityStateGetter.getWhiteOverlayProgress(livingState)));
            state.setStateData(StateDataTypes.BODY_YAW, livingState.bodyRot);
            state.setStateData(StateDataTypes.SCALE, livingState.scale * livingState.ageScale);
        }
        state.setStateData(ClientStateDataTypes.OUTLINE_COLOR, state.outlineColor);
        state.setStateData(ClientStateDataTypes.LIGHT, state.lightCoords);

        state.setStateData(StateDataTypes.TICK_DELTA, tickDelta);
        state.setStateData(StateDataTypes.CONTROLLERS, entity.getAnimationControllers());
        state.setStateData(StateDataTypes.MODEL_PROVIDER, getModelProvider());
    }

    public void updateControllerVariables(BRAnimationController controller, E entity, float tickDelta) { //todo add other queries
        controller.updateMolangEnvironment((environmentBuilder -> {
            environmentBuilder.setQuery("anim_time", (entity.tickCount + tickDelta) / 20f);
            environmentBuilder.setQuery("is_swimming", entity.isSwimming() ? 1 : 0);
            environmentBuilder.setQuery("camera_distance_range_lerp", Minecraft.getInstance().getCameraEntity() != null ? Minecraft.getInstance().getCameraEntity().distanceTo(entity) : 1);

            if (entity instanceof LivingEntity living) {
                environmentBuilder.setQuery("swim_amount", living.getSwimAmount(tickDelta));
                environmentBuilder.setQuery("can_climb", living.onClimbable() ? 1 : 0);
                environmentBuilder.setQuery("can_fly", living.canGlide() ? 1 : 0);
                environmentBuilder.setQuery("blocking", living.isBlocking() ? 1 : 0);
                if (living instanceof Mob mob) {
                    environmentBuilder.setQuery("can_damage_nearby_mobs", !mob.level().getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(5), target -> ((Mob) entity).canAttack(target)).isEmpty() ? 1 : 0);
                }
            }
            environmentBuilder.setQuery("can_power_jump", entity instanceof PlayerRideableJumping jumping && jumping.canJump() ? 1 : 0);
        }));
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
