package nordmods.biscuit_roll.model;

import gg.moonflower.pinwheel.api.geometry.GeometryModel;
import gg.moonflower.pinwheel.api.geometry.GeometryModelData;
import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.state.BRState;

public interface BRModelProvider<S extends BRState> {
    Identifier getModelId(S state); 
    Identifier getAnimationId(S state); 
    Identifier getTextureId(S state);
}
