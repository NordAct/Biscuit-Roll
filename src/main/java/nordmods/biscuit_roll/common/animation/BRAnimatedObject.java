package nordmods.biscuit_roll.common.animation;

import gg.moonflower.molangcompiler.api.bridge.MolangVariableProvider;

import java.util.Collection;

public interface BRAnimatedObject extends MolangVariableProvider {
    Collection<BRAnimationController> getAnimationControllers();
    boolean isClient();
}
