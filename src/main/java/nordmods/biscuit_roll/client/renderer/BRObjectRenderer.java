package nordmods.biscuit_roll.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import gg.moonflower.molangcompiler.api.MolangEnvironment;
import gg.moonflower.molangcompiler.api.MolangEnvironmentBuilder;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.Entity;
import nordmods.biscuit_roll.client.renderer.layer.BRRenderLayer;
import nordmods.biscuit_roll.common.animation.BRAnimatedObject;
import nordmods.biscuit_roll.common.animation.QueryHelper;
import nordmods.biscuit_roll.common.animation.controller.BRAnimationController;
import nordmods.biscuit_roll.common.model.BRModelProvider;
import nordmods.biscuit_roll.common.state.BRState;
import nordmods.biscuit_roll.common.state.StateDataTypes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/// Implementation of {@link BRRenderer} that can be used with any {@link BRAnimatedObject}
///
/// Use this if there's no existing implementation for your animated object
/// @see BREntityRenderer
/// @see BRBlockEntityRenderer
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
    /// @param state state that needs to be filled
    /// @param tickDelta transition progress between game ticks
    public void extractRenderState(O object, S state, float tickDelta) {
        object.getAnimationControllers().forEach(controller -> updateControllerVariables(controller.getEnvironment().edit(), object, tickDelta));
        state.setStateData(StateDataTypes.CONTROLLERS, object.getAnimationControllers());
        state.setStateData(StateDataTypes.MODEL_PROVIDER, getModelProvider());
        state.setStateData(StateDataTypes.ANIMATION_TIME, getAnimationTime(object, state, tickDelta));
    }

    /// Updates variables and queries for each controller during {@link BRObjectRenderer#extractRenderState(BRAnimatedObject, BRState, float)}.
    /// Note: `query.anim_time` gets updated during {@link nordmods.biscuit_roll.common.model.BRModel#applyAnimations(MolangEnvironment, Collection) regardless
    /// @param builder provided by controller Molang environment
    /// @param object animated object from which data for queries and values is gotten
    /// @param tickDelta current tick progress
    /// @see QueryHelper
    public void updateControllerVariables(MolangEnvironmentBuilder<?> builder, O object, float tickDelta) {}

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

    /// @return object's reference animation time in seconds
    public abstract float getAnimationTime(O object, S state, float tickDelta);

    @Override
    public String debugName() {
        return "Object";
    }
}
