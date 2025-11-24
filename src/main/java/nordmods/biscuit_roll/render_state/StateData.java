package nordmods.biscuit_roll.render_state;

import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StateData<T> {
    private final Identifier identifier;
    private final Class<T> clazz;

    public StateData(Identifier identifier, Class<T> clazz) {
        this.identifier = identifier;
        this.clazz = clazz;
    }

    public Holder<T> createHolder(@NotNull T value) {
        return new Holder<>(value);
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public record Holder<T>(@Nullable T value) {}
}
