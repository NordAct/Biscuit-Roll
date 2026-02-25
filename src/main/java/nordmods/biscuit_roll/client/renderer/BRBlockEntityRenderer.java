package nordmods.biscuit_roll.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import nordmods.biscuit_roll.client.renderer.layer.BRRenderLayer;
import nordmods.biscuit_roll.client.state.ClientStateDataTypes;
import nordmods.biscuit_roll.common.animation.BRAnimatedObject;
import nordmods.biscuit_roll.common.model.BRModelProvider;
import nordmods.biscuit_roll.common.state.StateDataTypes;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class BRBlockEntityRenderer<T extends BlockEntity & BRAnimatedObject, S extends BlockEntityRenderState> implements BlockEntityRenderer<T, S>, BRRenderer<S> {
    private final BRModelProvider modelProvider;
    private final List<BRRenderLayer> renderLayers = new ArrayList<>();

    protected BRBlockEntityRenderer(BRModelProvider modelProvider) {
        this.modelProvider = modelProvider;
    }

    @Override
    public void beforeSubmit(S state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, @Nullable CameraRenderState cameraRenderState) {
        poseStack.translate(0.5, 0, 0.5);
        poseStack.scale(-1, -1, 1);
    }

    @Override
    public void submit(S state, @NonNull PoseStack poseStack, @NonNull SubmitNodeCollector submitNodeCollector, @Nullable CameraRenderState cameraRenderState) {
        submitBRModel(state, poseStack, submitNodeCollector, cameraRenderState);
    }

    @Override
    public BRModelProvider getModelProvider() {
        return modelProvider;
    }

    @Override
    public Collection<BRRenderLayer> getRenderLayers() {
        return renderLayers;
    }

    @Override
    public void extractRenderState(T blockEntity, S state, float tickDelta, @NonNull Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, tickDelta, cameraPos, crumblingOverlay);
        state.setStateData(StateDataTypes.CONTROLLERS, blockEntity.getAnimationControllers());
        state.setStateData(StateDataTypes.MODEL_PROVIDER, getModelProvider());
        state.setStateData(StateDataTypes.ANIMATION_TIME, (getCurrentTick(blockEntity) / 20f) + tickDelta);
        state.setStateData(ClientStateDataTypes.CRUMBLING_OVERLAY, crumblingOverlay);
    }

    public abstract int getCurrentTick(T blockEntity);
}
