package nordmods.biscuit_roll.common.state;

import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

public record StateDataType<T>(Identifier identifier) {

    public Holder<T> createHolder(T value) {
        return new Holder<>(value);
    }

    public record Holder<T>(@Nullable T value) {
    }
}
