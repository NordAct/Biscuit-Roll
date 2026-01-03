package nordmods.biscuit_roll.common.state;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/// Biscuit Roll State, or for short BRState
///
/// Provides data map that stores values of different types. Uses [StateDataType] to define held value's type
public interface BRState {
    Map<StateDataType<?>, StateDataType.Holder<?>> getDataMap();

    /// @param stateDataType stored state data type to get
    /// @param <T> type of state data type and returned value
    /// @return same as [BRState#getStateData(StateDataType)], but wrapped as [Optional]
    @NotNull
    default <T> Optional<T> getStateDataOptional(@NotNull StateDataType<T> stateDataType) {
        return Optional.ofNullable(getStateData(stateDataType));
    }

    /// @param stateDataType stored state data type to get
    /// @param <T> type of state data type and returned value
    /// @return value from the state data type holder if value is present, null if specified state data type is not present
    @Nullable
    default <T> T getStateData(@NotNull StateDataType<T> stateDataType) {
        StateDataType.Holder<T> holder = (StateDataType.Holder<T>) getDataMap().get(stateDataType);
        return holder == null ? null : holder.value();
    }

    /// Stores specified value in data map
    /// @param stateDataType state data type, used as key for data map and to determine value's type
    /// @param value value to store
    /// @param <T> type of state data type and value
    default <T> void setStateData(@NotNull StateDataType<T> stateDataType, T value) {
        getDataMap().put(stateDataType, stateDataType.createHolder(value));
    }

    /// @param state state to copy
    /// @return new BRState with copy of data map of provided state
    static BRState copy(BRState state) {
        return new Impl(new HashMap<>(state.getDataMap()));
    }

    /// Basic implementation of BRState
    class Impl implements BRState {
        private final Map<StateDataType<?>, StateDataType.Holder<?>> dataMap;

        public Impl(Map<StateDataType<?>, StateDataType.Holder<?>> dataMap) {
            this.dataMap = dataMap;
        }

        @Override
        public Map<StateDataType<?>, StateDataType.Holder<?>> getDataMap() {
            return dataMap;
        }
    }
}
