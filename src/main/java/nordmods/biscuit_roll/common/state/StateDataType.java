package nordmods.biscuit_roll.common.state;

import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

/// Used to store data in data map in [BRState]
/// @param identifier state data type id
/// @param <T> type that defines type of held value in data map
/// @see StateDataTypes
/// @see nordmods.biscuit_roll.client.state.ClientStateDataTypes
public record StateDataType<T>(Identifier identifier) {

    /// @param value value to store
    /// @return new value holder
    public Holder<T> createHolder(T value) {
        return new Holder<>(value);
    }

    /// Used as value holder in data map in [BRState]
    /// @param value held value
    /// @param <T> value type
    public record Holder<T>(@Nullable T value) {
    }
}
