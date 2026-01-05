package nordmods.biscuit_roll.common.state;

import gg.moonflower.pinwheel.api.geometry.bone.AnimatedBone;
import nordmods.biscuit_roll.BiscuitRoll;
import nordmods.biscuit_roll.common.animation.BRAnimationController;
import nordmods.biscuit_roll.common.model.BRModelProvider;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.Map;

/// Build in [StateDataType] that can be called from both sides safely
public class StateDataTypes {
    public static final StateDataType<Float> TICK_DELTA = new StateDataType<>(BiscuitRoll.id("tick_delta"));
    public static final StateDataType<Float> BODY_YAW = new StateDataType<>(BiscuitRoll.id("body_yaw"));
    public static final StateDataType<Float> SCALE = new StateDataType<>(BiscuitRoll.id("scale"));
    public static final StateDataType<BRModelProvider> MODEL_PROVIDER = new StateDataType<>(BiscuitRoll.id("model_provider"));
    public static final StateDataType<Float> ANIMATION_TIME = new StateDataType<>(BiscuitRoll.id("animation_time"));
    public static final StateDataType<Collection<BRAnimationController>> CONTROLLERS = new StateDataType<>(BiscuitRoll.id("controllers"));
    @ApiStatus.Internal
    public static final StateDataType<Map<String, AnimatedBone.AnimationPose>> POSE_STORAGE = new StateDataType<>(BiscuitRoll.id("pose_storage"));
}
