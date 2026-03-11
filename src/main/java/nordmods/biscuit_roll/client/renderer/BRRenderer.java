package nordmods.biscuit_roll.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.client.internal.BRModelSubmitStorage;
import nordmods.biscuit_roll.client.renderer.layer.BRRenderLayer;
import nordmods.biscuit_roll.client.util.ClientModelManager;
import nordmods.biscuit_roll.common.animation.controller.BRAnimationController;
import nordmods.biscuit_roll.common.model.BRModel;
import nordmods.biscuit_roll.common.model.BRModelProvider;
import nordmods.biscuit_roll.common.state.BRState;
import nordmods.biscuit_roll.common.state.StateDataTypes;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.Collection;

/// Biscuit Roll Renderer or BRRenderer for short.
/// Base class for renderers
/// @see BRObjectRenderer
/// @see BREntityRenderer
public interface BRRenderer<S extends BRState> {
    /// @param state animated model state
    /// @return model for provided state
    @Nullable
    default BRModel getModel(S state) {
        return ClientModelManager.instance().getModel(getModelProvider().getModelId(state));
    }

    /// @return model provider for this for renderer
    BRModelProvider getModelProvider();

    /// @return render type that will be used when model gets submitted
    RenderType getRenderType(BRState state, Identifier texture);

    /// @return id of a texture that will be used during rendering
    Identifier getTextureId(BRState state);

    /// Runs before model actually gets submitted. By this moment, state should already be initialized, but transformations from animations are not applied
    ///
    /// @param state animated model state
    /// @param poseStack [gg.moonflower.pinwheel.api.transform.MatrixStack] that will be used during rendering
    /// @param submitNodeCollector node collector to which model will be submitted
    /// @param cameraRenderState camera render state
    default void beforeSubmit(S state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, @Nullable CameraRenderState cameraRenderState) {}

    /// Runs after model and render layers were submitted for render. By this moment transformations from animations are already applied to the model
    /// @param state animated model state
    /// @param poseStack [gg.moonflower.pinwheel.api.transform.MatrixStack] that was used
    /// @param submitNodeCollector node collector to which model was submitted
    /// @param cameraRenderState camera render state
    default void afterSubmit(S state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, @Nullable CameraRenderState cameraRenderState) {}

    /// Submits model for render with render order 0
    /// @param state animated model state
    /// @param poseStack [gg.moonflower.pinwheel.api.transform.MatrixStack] that will be used during rendering
    /// @param submitNodeCollector node collector to which model will be submitted
    /// @param cameraRenderState camera render state
    default void submitBRModel(S state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, @Nullable CameraRenderState cameraRenderState) {
        submit(state, poseStack, submitNodeCollector, cameraRenderState, (BRModelSubmitStorage) submitNodeCollector);
    }

    /// Submits model for render in specified order
    /// @param state animated model state
    /// @param poseStack [gg.moonflower.pinwheel.api.transform.MatrixStack] that will be used during rendering
    /// @param submitNodeCollector node collector to which model will be submitted
    /// @param cameraRenderState camera render state
    default void submitBRModelOrdered(S state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, @Nullable CameraRenderState cameraRenderState, int order) {
        submit(state, poseStack, submitNodeCollector, cameraRenderState, (BRModelSubmitStorage) submitNodeCollector.order(order));
    }

    /// Animates and submits model and its layers for render
    @ApiStatus.Internal
    @ApiStatus.NonExtendable
    default void submit(S state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, @Nullable CameraRenderState cameraRenderState, BRModelSubmitStorage brModelSubmitStorage) {
        poseStack.pushPose();

        beforeSubmit(state, poseStack, submitNodeCollector, cameraRenderState);

        Identifier texture = getTextureId(state);
        BRModel model = getModel(state);
        submitModel(poseStack, model, state, this::getRenderType, texture, brModelSubmitStorage);

        Collection<BRAnimationController> controllers = state.getStateData(StateDataTypes.CONTROLLERS);
        controllers.forEach(controller -> controller.update(state));
        state.setStateData(StateDataTypes.ANIMATION_ADJUSTMENT, this::adjustAnimation);
        model.applyAnimations(state); //vanilla does this as well because there's no other way to obtain accurate bone/locator transformations in render layers
        model.updateLocators();
        controllers.forEach(controller -> controller.triggerAnimationEffects(model, state));

        for (BRRenderLayer renderLayer : getRenderLayers()) {
            if (renderLayer.canRender(state)) renderLayer.submitLayer(BRState.copy(state), poseStack, submitNodeCollector, cameraRenderState);
        }
        afterSubmit(state, poseStack, submitNodeCollector, cameraRenderState);

        poseStack.popPose();
    }

    /// Called after model has animations from controller applied.
    /// Use this method to adjust model bone transforms or to change their visibility
    ///
    /// Note: each model is shared across all objects that use it.
    /// If you do disable visibility of certain bone on the model under certain condition,
    /// it *must* be updated each time this model is submitted to correctly update bone visibility
    /// @param state animated model state
    /// @param model model
    default void adjustAnimation(BRState state, BRModel model) {}

    /// @return all render layers this renderer has
    Collection<BRRenderLayer> getRenderLayers();

    /// @param renderLayer render layer to add
    default void addRenderLayer(BRRenderLayer renderLayer) {
        getRenderLayers().add(renderLayer);
    }

    /// Submits {@link BRModel} for render
    static void submitModel(
            PoseStack poseStack,
            BRModel model,
            BRState state,
            BRModelSubmitStorage.RenderTypeProvider renderTypeProvider,
            Identifier texture,
            BRModelSubmitStorage submitNodeCollector
    ) {
        if (model == null) {
            throw new IllegalStateException("Attempted to submit null model. Please check if model you trying to submit is loaded");
        }
        submitNodeCollector.biscuit_roll$submit(
                poseStack.last().copy(),
                model,
                state,
                renderTypeProvider,
                texture
        );
    }
}
