package nordmods.biscuit_roll.common.animation.controller;

import gg.moonflower.pinwheel.api.animation.AnimationData;
import nordmods.biscuit_roll.common.resource_managers.BRAnimationManager;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

public abstract class SideAwareAnimationController extends BRAnimationController{
    protected final Supplier<Boolean> isClient;
    public SideAwareAnimationController(boolean singleAnimation, Supplier<Boolean> isClient) {
        super(singleAnimation);
        this.isClient = isClient;
    }

    public boolean isClient() {
        return isClient.get();
    }

    @Nullable
    public AnimationData getAnimationData(String animation) {
        return animationFile == null ? null : BRAnimationManager.getAnimationManager(isClient()).getAnimation(animationFile, animation);
    }
}
