package nordmods.biscuit_roll.client.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import nordmods.biscuit_roll.client.internal.BRModelSubmitStorage;
import nordmods.biscuit_roll.client.renderer.BRRenderer;
import nordmods.biscuit_roll.client.util.ClientModelManager;
import nordmods.biscuit_roll.common.model.BRModel;
import nordmods.biscuit_roll.common.state.BRState;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

/// Biscuit Roll Render Layer or BRRender Layer for short.
/// Base class for render layer
/// 
/// All render layers are called _after_ main renderer submits the model, but before {@link BRRenderer#afterSubmit(BRState, PoseStack, SubmitNodeCollector, CameraRenderState)}.
/// When render layer is called, used model has already animation transformations applied to it
public abstract class BRRenderLayer {
    /// Renderer to which this layer is applied to
    protected final BRRenderer<?> parentRenderer;

    public BRRenderLayer(BRRenderer<?> parentRenderer) {
        this.parentRenderer = parentRenderer;
    }

    @ApiStatus.Internal
    public final void submitLayer(BRState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, @Nullable CameraRenderState cameraRenderState) {
        updateRenderState(state);
        poseStack.pushPose();
        beforeSubmit(state, poseStack, submitNodeCollector, cameraRenderState);
        submit(state, poseStack, submitNodeCollector, cameraRenderState);
        afterSubmit(state, poseStack, submitNodeCollector, cameraRenderState);
        poseStack.popPose();
    }

    /// Called before submitting the layer {@link BRRenderLayer#submit(BRState, PoseStack, SubmitNodeCollector, CameraRenderState)}
    /// @param state animated model state
    /// @param poseStack [gg.moonflower.pinwheel.api.transform.MatrixStack] that will be used during rendering
    /// @param submitNodeCollector node collector to which submission will be made
    /// @param cameraRenderState camera render state
    protected void beforeSubmit(BRState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, @Nullable CameraRenderState cameraRenderState) {}
    
    /// @param state animated model state
    /// @param poseStack [gg.moonflower.pinwheel.api.transform.MatrixStack] that will be used during rendering
    /// @param submitNodeCollector node collector to which submission will be made
    /// @param cameraRenderState camera render state
    protected abstract void submit(BRState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, @Nullable CameraRenderState cameraRenderState);

    /// Called after submitting the layer {@link BRRenderLayer#submit(BRState, PoseStack, SubmitNodeCollector, CameraRenderState)}. 
    /// Just like author of this library, this method exists for sake of existing
    /// @param state animated model state
    /// @param poseStack [gg.moonflower.pinwheel.api.transform.MatrixStack] that was used during rendering
    /// @param submitNodeCollector node collector to which submission was made
    /// @param cameraRenderState camera render state
    protected void afterSubmit(BRState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, @Nullable CameraRenderState cameraRenderState) {}

    /// Called before {@link BRRenderLayer#beforeSubmit(BRState, PoseStack, SubmitNodeCollector, CameraRenderState)}. 
    /// Used to update or add any extra information to state data
    /// @param state copy of state after parent renderer has submitted the model
    protected void updateRenderState(BRState state) {}

    /// @param state animated model state
    /// @return model used by parent renderer from provided state
    @Nullable
    protected final BRModel getModel(BRState state) {
        return ClientModelManager.instance().getModel(this.parentRenderer.getModelProvider().getModelId(state));
    }

    /// Defines if layer can even be submitted during {@link BRRenderer#submit(BRState, PoseStack, SubmitNodeCollector, CameraRenderState, BRModelSubmitStorage)}
    /// @param state animated model state
    public boolean canRender(BRState state) {
        return true;
    }
}
