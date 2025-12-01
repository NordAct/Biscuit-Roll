package nordmods.biscuit_roll.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import nordmods.biscuit_roll.client.internal.BRModelSubmitStorage;
import nordmods.biscuit_roll.client.state.ClientStateDataTypes;
import nordmods.biscuit_roll.common.model.BRModelProvider;
import org.jetbrains.annotations.ApiStatus;

public abstract class BREntityRenderer<E extends Entity, S extends EntityRenderState> extends EntityRenderer<E, S> implements BRRenderer<S>{
    private final BRModelProvider<S> modelProvider;
    private final LivingRenderStateGetter<LivingEntity, LivingEntityRenderState, EntityModel<LivingEntityRenderState>> livingEntityStateGetter;
    private final MobRenderStateGetter<Mob, LivingEntityRenderState, EntityModel<LivingEntityRenderState>> mobRenderStateGetter;

    protected BREntityRenderer(EntityRendererProvider.Context context, BRModelProvider<S> modelProvider) {
        super(context);
        this.modelProvider = modelProvider;
        this.livingEntityStateGetter = new LivingRenderStateGetter<>(context);
        this.mobRenderStateGetter = new MobRenderStateGetter<>(context);
    }

    public void submit(S state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        poseStack.pushPose();
        ((BRModelSubmitStorage)submitNodeCollector).biscuit_roll$submit(
                poseStack.last().copy(),
                getModel(state),
                state,
                getRenderType(state, getModelProvider().getTextureId(state))
        );
        poseStack.popPose();
        super.submit(state, poseStack, submitNodeCollector, cameraRenderState);
    }

    @Override
    public BRModelProvider<S> getModelProvider() {
        return modelProvider;
    }

    public abstract RenderType getRenderType(S state, Identifier texture);

    @Override
    public void extractRenderState(E entity, S state, float tickDelta) {
        super.extractRenderState(entity, state, tickDelta);
        if (state instanceof LivingEntityRenderState livingState && entity instanceof LivingEntity livingEntity) {
            livingEntityStateGetter.fillRenderState(livingEntity, livingState, tickDelta);
            if (entity instanceof Mob mob) mobRenderStateGetter.fillRenderState(mob, livingState, tickDelta);
            state.setStateData(ClientStateDataTypes.OVERLAY_TEXTURE, LivingEntityRenderer.getOverlayCoords(livingState, livingEntityStateGetter.getWhiteOverlayProgress(livingState)));
        }
        state.setStateData(ClientStateDataTypes.OUTLINE_COLOR, state.outlineColor);
        state.setStateData(ClientStateDataTypes.LIGHT, state.lightCoords);
    }

    @ApiStatus.Internal
    private record LivingRenderStateGetter<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>>(LivingEntityRenderer<T, S, M> renderer) {
        private LivingRenderStateGetter(EntityRendererProvider.Context renderer) {
            this(new LivingEntityRenderer<>(renderer, null, 0) {
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

        public float getWhiteOverlayProgress(S state) {
            return renderer.getWhiteOverlayProgress(state);
        }
    }

    @ApiStatus.Internal
    private record MobRenderStateGetter<T extends Mob, S extends LivingEntityRenderState, M extends EntityModel<? super S>>(MobRenderer<T, S, M> renderer) {
        private MobRenderStateGetter(EntityRendererProvider.Context renderer) {
            this(new MobRenderer<>(renderer, null, 0) {
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
