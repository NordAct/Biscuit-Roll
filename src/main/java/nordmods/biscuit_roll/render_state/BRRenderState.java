package nordmods.biscuit_roll.render_state;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public interface BRRenderState {
    Map<StateDataType<?>, StateDataType.Holder<?>> getDataMap();

    @NotNull
    default <T> Optional<T> getStateData(@NotNull StateDataType<T> stateDataType) {
        return Optional.ofNullable((T)getDataMap().get(stateDataType));
    }

    default <T> void setStateData(@NotNull StateDataType<T> stateDataType, T value) {
        getDataMap().put(stateDataType, stateDataType.createHolder(value));
    }

    class Impl implements BRRenderState {
        private final Map<StateDataType<?>, StateDataType.Holder<?>> dataMap = new HashMap<>();

        @Override
        public Map<StateDataType<?>, StateDataType.Holder<?>> getDataMap() {
            return dataMap;
        }
    }
}
