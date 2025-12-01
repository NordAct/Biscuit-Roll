package nordmods.biscuit_roll.client.internal;

import nordmods.biscuit_roll.common.state.BRState;
import nordmods.biscuit_roll.common.state.StateDataType;

import java.util.Map;

public interface InjectedBRState extends BRState {
    default Map<StateDataType<?>, StateDataType.Holder<?>> getDataMap() {
        return biscuit_roll$getInjectedDataMap();
    }

    Map<StateDataType<?>, StateDataType.Holder<?>> biscuit_roll$getInjectedDataMap();
}
