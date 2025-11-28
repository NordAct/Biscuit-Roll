package nordmods.biscuit_roll.render_state;

import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record StateDataType<T>(Identifier identifier) {

    public Holder<T> createHolder(@NotNull T value) {
        return new Holder<>(value);
    }

    public record Holder<T>(@Nullable T value) {
    }
}
