package nordmods.biscuit_roll.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.CameraRenderState;
import nordmods.biscuit_roll.client.renderer.layer.BRRenderLayer;
import nordmods.biscuit_roll.common.animation.BRAnimatedObject;
import nordmods.biscuit_roll.common.model.BRModelProvider;
import nordmods.biscuit_roll.common.state.BRState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/// Implementation of {@link BRRenderer} that can be used with any {@link BRAnimatedObject}
///
/// Use this if there's no existing implementation for your animated object
/// @see BREntityRenderer
public abstract class BRObjectRenderer<O extends BRAnimatedObject, S extends BRState> implements BRRenderer<S> {
    private final BRModelProvider modelProvider;
    private final List<BRRenderLayer> renderLayers = new ArrayList<>();

    public BRObjectRenderer(BRModelProvider modelProvider) {
        this.modelProvider = modelProvider;
    }

    @Override
    public BRModelProvider getModelProvider() {
        return modelProvider;
    }

    @Override
    public Collection<BRRenderLayer> getRenderLayers() {
        return renderLayers;
    }

    /// Fills state for render by getting information from object
    /// @param object object from which state data can be obtained
    /// @param renderState state that needs to be filled
    /// @param tickDelta transition progress between game ticks
    public abstract void extractRenderState(O object, S renderState, float tickDelta);

    /// @return new {@link BRState}
    public abstract S createRenderState();

    /// Submits object for renderer with order 0
    public void submitObject(O object, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState, float tickDelta) {
        S state = createRenderState();
        extractRenderState(object, state, tickDelta);
        submitBRModel(state, poseStack, submitNodeCollector, cameraRenderState);
    }

    /// Submits object for renderer with specified order
    public void submitObjectOrdered(O object, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState, float tickDelta, int order) {
        S state = createRenderState();
        extractRenderState(object, state, tickDelta);
        submitBRModelOrdered(state, poseStack, submitNodeCollector, cameraRenderState, order);
    }
}
