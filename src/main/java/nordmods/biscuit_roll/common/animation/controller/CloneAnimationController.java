package nordmods.biscuit_roll.common.animation.controller;

import gg.moonflower.pinwheel.api.animation.AnimationData;
import nordmods.biscuit_roll.common.animation.BRAnimatedObject;
import nordmods.biscuit_roll.common.animation.BRPlayingAnimation;
import nordmods.biscuit_roll.common.model.BRModel;
import nordmods.biscuit_roll.common.state.BRState;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Animation controller that doesn't have any of its own animations, but rather "copies" them from other controllers.
 * It doesn't update playing animations, leaving this job to controller from which it copied those animations.
 * All it does is making sure it doesn't keep done animations that are done on other controller so garbage collector can do its job
 */
public class CloneAnimationController extends BRAnimationController {
    public CloneAnimationController(boolean isClient) {
        super(isClient, false);
    }

    @Override
    protected void onSoundEffect(AnimationData.SoundEffect soundEffect, BRModel model, BRState state) {}

    @Override
    protected void onParticleEffect(AnimationData.ParticleEffect particleEffect, BRModel model, BRState state) {}

    @Override
    protected void onTimelineEffect(AnimationData.TimelineEffect timelineEffect, BRModel model, BRState state) {}

    @Override
    public void tick() {
        Map<String, BRPlayingAnimation> shouldContinue = new HashMap<>();
        playingAnimations.forEach((name, animation) -> {
            if (!animation.isDone() || !animation.canClearOut()) shouldContinue.put(name, animation);
        });
        playingAnimations.clear();
        playingAnimations.putAll(shouldContinue);
    }

    @Override
    public void update(BRState state) {
        tick();
    }

    @Override
    public void playAnimation(String animation) {}

    @Override
    public void playAnimation(String animation, float transitionInTime, float transitionOutTime, AnimationData.LerpMode transitionInLerp, AnimationData.LerpMode transitionOutLerp) {}

    @Override
    public void setAnimationTime(float time) {}

    public void copyFrom(BRAnimationController controller) {
        playingAnimations.putAll(controller.playingAnimations);
    }

    public void copyFrom(Collection<BRAnimationController> controllers) {
        controllers.forEach(this::copyFrom);
    }

    public void copyFrom(BRAnimatedObject animatedObject) {
        copyFrom(animatedObject.getAnimationControllers());
    }
}
