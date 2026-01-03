package nordmods.biscuit_roll.common.model;

import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.common.state.BRState;

/// Provides animation and model file ids to be used
public interface BRModelProvider {
    /// @param state animated model state
    /// @return model file id
    Identifier getModelId(BRState state);

    /// @param state animated model state
    /// @return animation file id
    Identifier getAnimationId(BRState state);
}
