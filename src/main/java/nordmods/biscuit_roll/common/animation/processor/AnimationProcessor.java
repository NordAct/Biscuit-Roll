package nordmods.biscuit_roll.common.animation.processor;

import gg.moonflower.molangcompiler.api.MolangEnvironmentBuilder;
import gg.moonflower.pinwheel.api.geometry.GeometryCompileException;
import nordmods.biscuit_roll.common.animation.BRAnimationProcessorOwner;
import nordmods.biscuit_roll.common.animation.controller.BRAnimationController;
import nordmods.biscuit_roll.common.animation.controller.CloneAnimationController;
import nordmods.biscuit_roll.common.model.BRModel;
import nordmods.biscuit_roll.common.model.BRModelProvider;
import nordmods.biscuit_roll.common.resource_managers.BRModelManager;
import nordmods.biscuit_roll.common.state.BRState;
import nordmods.biscuit_roll.common.state.StateDataTypes;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public abstract class AnimationProcessor<T extends BRAnimationProcessorOwner<T>> {
    protected final T animatable;
    protected final BRState state = new BRState.Impl(new HashMap<>());
    @Nullable
    private BRModel model;
    //in case if this runs on client too to avoid concurrent modification of animations during rendering
    private final CloneAnimationController cloneController = new CloneAnimationController();
    private final List<BRAnimationController> cloneControllerList = List.of(cloneController);
    public AnimationProcessor(T animatable) {
        this.animatable = animatable;
    }

    public void updateBRState() {
        state.setStateData(StateDataTypes.CONTROLLERS, getAnimationControllers());
        getAnimationControllers().forEach(controller -> updateControllerVariables(controller.getEnvironment().edit(), animatable, 1));
        state.setStateData(StateDataTypes.MODEL_PROVIDER, getModelProvider());
        state.setStateData(StateDataTypes.ANIMATION_TIME, getAnimationTime());
    }

    public void updateControllerVariables(MolangEnvironmentBuilder<?> builder, T animatable, float tickDelta) {}

    public BRState getState() {
        return state;
    }

    public void tick() {
        if (isClient()) {
            cloneController.copyFrom(animatable);
        }
        updateBRState();
        BRModel model = getModel();
        Collection<BRAnimationController> controllers = state.getStateData(StateDataTypes.CONTROLLERS);
        controllers.forEach(c -> c.update(state));
        state.setStateData(StateDataTypes.ANIMATION_ADJUSTMENT, this::adjustAnimation);
        model.applyAnimations(state);
        controllers.forEach(c -> c.triggerAnimationEffects(model, state));
        postAnimation();
    }

    public List<BRAnimationController> getAnimationControllers() {
        return isClient() ? cloneControllerList : animatable.getAnimationControllers();
    }

    @NonNull
    public BRModel getModel() {
        if (model == null) {
            BRModelProvider modelProvider = state.getStateData(StateDataTypes.MODEL_PROVIDER);
            if (modelProvider == null) throw new IllegalStateException("Cannot get model before model provider is provided");
            try {
                model = new BRModel(BRModelManager.getModelManager(false).getModelData(modelProvider.getModelId(state)), null);
            } catch (GeometryCompileException e) {
                throw new RuntimeException(e);
            }
        }
        return model;
    }

    @Nullable
    public BRModel getModelDirect() {
        return model;
    }

    public abstract BRModelProvider getModelProvider();

    public float getAnimationTime() {
        return animatable.getAnimationTime();
    }

    public boolean isClient() {
        return animatable.isClient();
    }

    public void adjustAnimation(BRState state, BRModel model){}

    public void postAnimation() {}
}
