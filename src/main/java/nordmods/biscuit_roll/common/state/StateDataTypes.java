package nordmods.biscuit_roll.common.state;

import nordmods.biscuit_roll.BiscuitRoll;

public class StateDataTypes {
    public static final StateDataType<Float> TICK_DELTA = new StateDataType<>(BiscuitRoll.id("tick_delta"));
    public static final StateDataType<Float> BODY_YAW = new StateDataType<>(BiscuitRoll.id("body_yaw"));
    public static final StateDataType<Float> SCALE = new StateDataType<>(BiscuitRoll.id("scale"));
}
