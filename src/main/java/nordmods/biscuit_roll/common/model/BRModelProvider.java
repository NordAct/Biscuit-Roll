package nordmods.biscuit_roll.common.model;

import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.common.state.BRState;

public interface BRModelProvider {
    Identifier getModelId(BRState state);
    Identifier getAnimationId(BRState state);
}
