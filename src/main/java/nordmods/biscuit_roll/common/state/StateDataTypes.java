package nordmods.biscuit_roll.common.state;

import nordmods.biscuit_roll.BiscuitRoll;
import nordmods.biscuit_roll.common.animation.controller.BRAnimationController;
import nordmods.biscuit_roll.common.model.BRModel;
import nordmods.biscuit_roll.common.model.BRModelProvider;

import java.util.List;
import java.util.function.BiConsumer;

/// Build in [StateDataType] that can be called from both sides safely
public class StateDataTypes {
    /// Current tick delta
    public static final StateDataType<Float> TICK_DELTA = new StateDataType<>(BiscuitRoll.id("tick_delta"));
    /// Animated entity's body yaw
    public static final StateDataType<Float> BODY_YAW = new StateDataType<>(BiscuitRoll.id("body_yaw"));
    /// Animated object's scale
    public static final StateDataType<Float> SCALE = new StateDataType<>(BiscuitRoll.id("scale"));
    /// Model provider to get model and animation data
    public static final StateDataType<BRModelProvider> MODEL_PROVIDER = new StateDataType<>(BiscuitRoll.id("model_provider"));
    /// Animated object's reference animation time
    public static final StateDataType<Float> ANIMATION_TIME = new StateDataType<>(BiscuitRoll.id("animation_time"));
    /// Animated object's animation controllers
    public static final StateDataType<List<BRAnimationController>> CONTROLLERS = new StateDataType<>(BiscuitRoll.id("controllers"));
    /// Used for adjusting model bone transforms after controller animations have been applied
    public static final StateDataType<BiConsumer<BRState, BRModel>> ANIMATION_ADJUSTMENT = new StateDataType<>(BiscuitRoll.id("animation_adjustment"));

}
