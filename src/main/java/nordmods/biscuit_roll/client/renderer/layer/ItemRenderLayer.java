package nordmods.biscuit_roll.client.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import gg.moonflower.pinwheel.api.transform.LocatorTransformation;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import nordmods.biscuit_roll.client.renderer.BRRenderer;
import nordmods.biscuit_roll.client.state.ClientStateDataTypes;
import nordmods.biscuit_roll.common.state.BRState;
import org.jetbrains.annotations.Nullable;

public abstract class ItemRenderLayer extends BRRenderLayer{
    public ItemRenderLayer(BRRenderer<?> parentRenderer) {
        super(parentRenderer);
    }

    @Override
    protected void beforeSubmit(BRState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        ItemStackRenderState stackRenderState = getItemStackRenderState(state);
        if (stackRenderState == null) return;
        LocatorTransformation transformation = getModel(state).getLocatorTransformation(getLocatorName());
        poseStack.scale(-1, -1, 1);
        poseStack.mulPose(transformation.matrix());
        poseStack.scale(1, -1, -1);
    }

    @Override
    protected void submit(BRState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        ItemStackRenderState stackRenderState = getItemStackRenderState(state);
        if (stackRenderState == null) return;
        stackRenderState.submit(poseStack, submitNodeCollector, state.getStateData(ClientStateDataTypes.LIGHT), OverlayTexture.NO_OVERLAY, 0);
    }

    protected abstract String getLocatorName();

    @Nullable
    protected abstract ItemStackRenderState getItemStackRenderState(BRState state);
}
