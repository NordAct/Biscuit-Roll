package nordmods.biscuit_roll.common.model;

import gg.moonflower.pinwheel.api.animation.AnimationData;
import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.common.state.BRState;
import nordmods.biscuit_roll.common.util.BRAnimationManager;

public interface BRModelProvider {
    Identifier getModelId(BRState state);
    Identifier getAnimationId(BRState state);

    default AnimationData getAnimationData(Identifier animationId, boolean isClient, String animation) {
        return BRAnimationManager.getAnimationManager(isClient).getAnimation(animationId, animation);
    }
}
