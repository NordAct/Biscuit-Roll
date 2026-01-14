package nordmods.biscuit_roll.client.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import nordmods.biscuit_roll.client.renderer.BRRenderer;
import nordmods.biscuit_roll.client.state.ClientStateDataTypes;
import nordmods.biscuit_roll.common.state.BRState;
import org.jspecify.annotations.Nullable;

/// Submits {@link ItemStackRenderState} to be rendered at specified locator in {@link ItemRenderLayer#getLocatorName()}
public abstract class ItemRenderLayer extends BRRenderLayer{
    public ItemRenderLayer(BRRenderer<?> parentRenderer) {
        super(parentRenderer);
    }

    @Override
    protected void beforeSubmit(BRState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        poseStack.scale(-1, -1, 1);
        poseStack.mulPose(getModel(state).getLocatorTransformation(getLocatorName()).matrix());
        poseStack.scale(-1, -1, 1);
    }

    @Override
    protected void submit(BRState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        getItemStackRenderState(state).submit(poseStack, submitNodeCollector, state.getStateData(ClientStateDataTypes.LIGHT), OverlayTexture.NO_OVERLAY, 0);
    }

    /// @return name of the locator where item will be rendered
    protected abstract String getLocatorName();

    /// {@link ItemStackRenderState} should be created either before this layer gets submitted in parent renderer or in {@link BRRenderLayer#updateRenderState(BRState)} as this method is called several times before actually submitting
    /// @return render state of an item to be rendered
    @Nullable
    protected abstract ItemStackRenderState getItemStackRenderState(BRState state);

    @Override
    public boolean canRender(BRState state) {
        return getItemStackRenderState(state) != null && getModel(state).getLocatorTransformation(getLocatorName()) != null;
    }
}
