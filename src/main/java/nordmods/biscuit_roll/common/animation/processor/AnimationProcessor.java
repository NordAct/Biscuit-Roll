package nordmods.biscuit_roll.common.animation.processor;

import gg.moonflower.molangcompiler.api.MolangEnvironment;
import gg.moonflower.molangcompiler.api.MolangEnvironmentBuilder;
import gg.moonflower.pinwheel.api.geometry.GeometryCompileException;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import nordmods.biscuit_roll.client.renderer.BREntityRenderer;
import nordmods.biscuit_roll.common.animation.BRAnimationProcessorOwner;
import nordmods.biscuit_roll.common.animation.QueryHelper;
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

/// Holds its own model and processes animation independently from model renderer.
/// Main use case is server side animation and model processing, but also can be used just for animation synchronization between server and client.
///
/// @see BRAnimationProcessorOwner
/// @see nordmods.biscuit_roll.common.animation.BREntityWithAnimationProcessor
/// @see nordmods.biscuit_roll.common.animation.BRBlockEntityWithAnimationProcessor
/// @param <T> animated object
public abstract class AnimationProcessor<T extends BRAnimationProcessorOwner<T>> {
    protected final T animatable;
    protected final BRState state = new BRState.Impl(new HashMap<>());
    @Nullable
    private BRModel model;
    //in case if this runs on client too to avoid concurrent modification of animations during rendering
    private final CloneAnimationController cloneController = new CloneAnimationController();
    private final List<BRAnimationController> cloneControllerList = List.of(cloneController);

    /// @param animatable animated object
    public AnimationProcessor(T animatable) {
        this.animatable = animatable;
    }

    /// Fills processor's state with required data
    public void updateBRState() {
        state.setStateData(StateDataTypes.CONTROLLERS, getAnimationControllers());
        getAnimationControllers().forEach(controller -> updateControllerVariables(controller.getEnvironment().edit(), animatable, 1));
        state.setStateData(StateDataTypes.MODEL_PROVIDER, getModelProvider());
        state.setStateData(StateDataTypes.ANIMATION_TIME, getAnimationTime());
    }

    /// Updates variables and queries for each controller during {@link BREntityRenderer#extractRenderState(Entity, EntityRenderState, float)}.
    /// Note: `query.anim_time` gets updated during {@link BRModel#applyAnimations(MolangEnvironment, Collection)} regardless
    /// @param builder provided by controller Molang environment
    /// @param animatable animated object from which data for queries and values is gotten
    /// @param tickDelta current tick progress
    /// @see QueryHelper
    public void updateControllerVariables(MolangEnvironmentBuilder<?> builder, T animatable, float tickDelta) {}

    /// @return state used by processor
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
        if (processModel()) {
            state.setStateData(StateDataTypes.ANIMATION_ADJUSTMENT, this::adjustAnimation);
            model.applyAnimations(state);
        }
        controllers.forEach(c -> c.triggerAnimationEffects(model, state));
    }

    /// @return collection of animation controllers
    public List<BRAnimationController> getAnimationControllers() {
        return isClient() ? cloneControllerList : animatable.getAnimationControllers();
    }

    /// @return model if it's present or attempts to get model if it's not
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

    /// @return gets model directly even if it's not present
    @Nullable
    public BRModel getModelDirect() {
        return model;
    }

    /// @return model provider
    public abstract BRModelProvider getModelProvider();

    /// @return animated object's reference animation time
    public float getAnimationTime() {
        return animatable.getAnimationTime();
    }

    /// @return current logical side
    public boolean isClient() {
        return animatable.isClient();
    }

    /// Called after model has animations from controller applied.
    /// Use this method to adjust model bone transforms.
    /// Unlike with renderer, processor's model is unique for each animation processor
    /// If {@link AnimationProcessor#processModel()} is set to false, this method will never be called
    ///
    /// @param state animated model state
    /// @param model model
    public void adjustAnimation(BRState state, BRModel model){}

    /// @return if processor should update model transformations as well. If it shouldn't, only controllers will be updated
    public boolean processModel() {
        return false;
    }
}
