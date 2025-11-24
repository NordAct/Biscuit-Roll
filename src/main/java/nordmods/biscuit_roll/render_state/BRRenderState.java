package nordmods.biscuit_roll.render_state;

import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public interface BRRenderState {
    Map<Identifier, StateData.Holder<?>> getDataMap();

    @NotNull
    default <T> Optional<T> getStateData(@NotNull StateData<T> stateData) {
        return Optional.ofNullable((T)getDataMap().get(stateData.getIdentifier()));
    }

    default <T> void setStateData(@NotNull StateData<T> stateData, T value) {
        getDataMap().put(stateData.getIdentifier(), stateData.createHolder(value));
    }

    class Impl implements BRRenderState {
        private final Map<Identifier, StateData.Holder<?>> dataMap = new HashMap<>();

        @Override
        public Map<Identifier, StateData.Holder<?>> getDataMap() {
            return dataMap;
        }
    }
}
