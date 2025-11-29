package nordmods.biscuit_roll.common.state;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public interface BRState {
    Map<StateDataType<?>, StateDataType.Holder<?>> getDataMap();

    @NotNull
    default <T> Optional<T> getStateData(@NotNull StateDataType<T> stateDataType) {
        StateDataType.Holder<T> holder = (StateDataType.Holder<T>) getDataMap().getOrDefault(stateDataType, null);
        return holder == null ? Optional.empty() : Optional.ofNullable(holder.value());
    }

    default <T> void setStateData(@NotNull StateDataType<T> stateDataType, T value) {
        getDataMap().put(stateDataType, stateDataType.createHolder(value));
    }

    class Impl implements BRState {
        private final Map<StateDataType<?>, StateDataType.Holder<?>> dataMap = new HashMap<>();

        @Override
        public Map<StateDataType<?>, StateDataType.Holder<?>> getDataMap() {
            return dataMap;
        }
    }
}
