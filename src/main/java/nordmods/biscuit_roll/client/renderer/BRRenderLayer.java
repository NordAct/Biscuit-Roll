package nordmods.biscuit_roll.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.CameraRenderState;
import nordmods.biscuit_roll.common.state.BRState;
import org.jetbrains.annotations.ApiStatus;

public abstract class BRRenderLayer {
    protected final BRRenderer<?> parentRenderer;

    public BRRenderLayer(BRRenderer<?> parentRenderer) {
        this.parentRenderer = parentRenderer;
    }


    @ApiStatus.Internal
    public final void submitLayer(BRState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        updateRenderState(state);
        poseStack.pushPose();
        beforeSubmit(state, poseStack, submitNodeCollector, cameraRenderState);
        submit(state, poseStack, submitNodeCollector, cameraRenderState);
        afterSubmit(state, poseStack, submitNodeCollector, cameraRenderState);
        poseStack.popPose();
    }

    protected void beforeSubmit(BRState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {}

    protected abstract void submit(BRState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState);

    protected void afterSubmit(BRState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {}

    protected void updateRenderState(BRState state) {

    }
}
