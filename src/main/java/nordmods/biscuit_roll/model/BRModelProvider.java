package nordmods.biscuit_roll.model;

import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.render_state.BRRenderState;

public interface BRModelProvider<S extends BRRenderState> {
    Identifier getModelId(S state); 
    Identifier getAnimationId(S state); 
    Identifier getTextureId(S state); 
}
