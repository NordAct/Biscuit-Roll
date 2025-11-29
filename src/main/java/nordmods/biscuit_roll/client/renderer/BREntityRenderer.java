package nordmods.biscuit_roll.client.renderer;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import nordmods.biscuit_roll.client.internal.BRModelSubmitStorage;
import nordmods.biscuit_roll.client.state.ClientStateDataTypes;
import nordmods.biscuit_roll.common.model.BRModelProvider;
import org.jetbrains.annotations.NotNull;

public abstract class BREntityRenderer<E extends Entity, S extends EntityRenderState> extends EntityRenderer<@NotNull E, @NotNull S> implements BRRenderer<S>{
    private final BRModelProvider<S> modelProvider;

    protected BREntityRenderer(EntityRendererProvider.Context context, BRModelProvider<S> modelProvider) {
        super(context);
        this.modelProvider = modelProvider;
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
        state.setStateData(ClientStateDataTypes.OUTLINE_COLOR, state.outlineColor);
        state.setStateData(ClientStateDataTypes.LIGHT, state.lightCoords);
        if (state instanceof LivingEntityRenderState livingState) {
            state.setStateData(ClientStateDataTypes.OVERLAY_TEXTURE, LivingEntityRenderer.getOverlayCoords(livingState, 0));
        }
    }
}
