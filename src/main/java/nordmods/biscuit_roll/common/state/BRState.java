package nordmods.biscuit_roll.common.state;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public interface BRState {
    Map<StateDataType<?>, StateDataType.Holder<?>> getDataMap();

    @NotNull
    default <T> Optional<T> getStateDataOptional(@NotNull StateDataType<T> stateDataType) {
        return Optional.ofNullable(getStateData(stateDataType));
    }

    @Nullable
    default <T> T getStateData(@NotNull StateDataType<T> stateDataType) {
        StateDataType.Holder<T> holder = (StateDataType.Holder<T>) getDataMap().get(stateDataType);
        return holder == null ? null : holder.value();
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
