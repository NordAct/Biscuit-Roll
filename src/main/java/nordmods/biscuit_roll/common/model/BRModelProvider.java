package nordmods.biscuit_roll.common.model;

import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.common.state.BRState;

public interface BRModelProvider<S extends BRState> {
    Identifier getModelId(S state); 
    Identifier getAnimationId(S state); 
    Identifier getTextureId(S state);
}
