package nordmods.biscuit_roll.common.animation;

import nordmods.biscuit_roll.common.model.BRModel;
import nordmods.biscuit_roll.common.state.BRState;

import java.util.Objects;

public interface EffectConsumer<E> {
    void accept(E effect, BRModel model, BRState state);

    default EffectConsumer<E> andThen(EffectConsumer<E> after) {
        Objects.requireNonNull(after);

        return (effect, model, state) -> {
            accept(effect, model, state);
            after.accept(effect, model, state);
        };
    }
}
