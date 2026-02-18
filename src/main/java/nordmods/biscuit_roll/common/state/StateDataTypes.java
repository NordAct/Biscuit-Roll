package nordmods.biscuit_roll.common.state;

import nordmods.biscuit_roll.BiscuitRoll;
import nordmods.biscuit_roll.common.animation.controller.BRAnimationController;
import nordmods.biscuit_roll.common.model.BRModel;
import nordmods.biscuit_roll.common.model.BRModelProvider;

import java.util.Collection;
import java.util.function.BiConsumer;

/// Build in [StateDataType] that can be called from both sides safely
public class StateDataTypes {
    public static final StateDataType<Float> TICK_DELTA = new StateDataType<>(BiscuitRoll.id("tick_delta"));
    public static final StateDataType<Float> BODY_YAW = new StateDataType<>(BiscuitRoll.id("body_yaw"));
    public static final StateDataType<Float> SCALE = new StateDataType<>(BiscuitRoll.id("scale"));
    public static final StateDataType<BRModelProvider> MODEL_PROVIDER = new StateDataType<>(BiscuitRoll.id("model_provider"));
    public static final StateDataType<Float> ANIMATION_TIME = new StateDataType<>(BiscuitRoll.id("animation_time"));
    public static final StateDataType<Collection<BRAnimationController>> CONTROLLERS = new StateDataType<>(BiscuitRoll.id("controllers"));
    /// Used for adjusting model bone transforms after controller animations have been applied
    public static final StateDataType<BiConsumer<BRState, BRModel>> ANIMATION_ADJUSTMENT = new StateDataType<>(BiscuitRoll.id("animation_adjustment"));

}
