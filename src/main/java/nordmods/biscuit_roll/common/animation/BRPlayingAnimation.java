package nordmods.biscuit_roll.common.animation;

import gg.moonflower.pinwheel.api.animation.AnimationData;
import gg.moonflower.pinwheel.impl.animation.PlayingAnimationImpl;

public class BRPlayingAnimation extends PlayingAnimationImpl {
    private final float startTime;
    public BRPlayingAnimation(AnimationData animation, float startTime) {
        super(animation);
        this.startTime = startTime;
    }

    @Override
    public float getAnimationTime() {
        return super.getAnimationTime() - startTime;
    }
}
