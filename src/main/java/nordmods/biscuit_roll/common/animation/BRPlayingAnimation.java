package nordmods.biscuit_roll.common.animation;

import gg.moonflower.molangcompiler.api.MolangEnvironment;
import gg.moonflower.pinwheel.api.animation.AnimationData;
import gg.moonflower.pinwheel.api.animation.PlayingAnimation;
import net.minecraft.util.EasingType;

public class BRPlayingAnimation implements PlayingAnimation {
    private final AnimationData animation;
    private float time;
    private final float startTime;
    private final float transitionInTime;
    private final float transitionOutTime;
    private final EasingType transitionInEasing;
    private final EasingType transitionOutEasing;

    private float weight = 1;
    private boolean stopped = false;
    private boolean paused = false;
    private float stopTime;
    public BRPlayingAnimation(AnimationData animation, float startTime, float transitionInTime, float transitionOutTime, EasingType transitionInEasing, EasingType transitionOutEasing) {
        this.animation = animation;
        this.startTime = startTime;
        this.transitionInTime = Math.max(0, transitionInTime);
        this.transitionOutTime = Math.max(0, transitionOutTime);
        this.transitionInEasing = transitionInEasing;
        this.transitionOutEasing = transitionOutEasing;
    }

    @Override
    public float getAnimationTime() {
        return isTransitioningIn() ? 0 : isTransitioningOut() ? getAnimationDuration() : time - startTime - transitionInTime;
    }

    public float getTransitionInProgress() {
        return transitionInTime > 0 ? transitionInEasing.apply((time - startTime) / transitionInTime) : 1;
    }

    public float getTransitionOutProgress() {
        return transitionOutTime > 0 ?
                transitionOutEasing.apply((transitionOutTime - (time - getAnimationDuration())) / transitionOutTime) :
                1;
    }

    public boolean isTransitioningIn() {
        return time - startTime <= transitionInTime;
    }

    public boolean isTransitioningOut() {
        if (stopped) return true;
        float transitionTime = time - getAnimationDuration();
        return transitionTime > 0 && transitionTime <= transitionOutTime;
    }

    @Override
    public AnimationData getAnimation() {
        return animation;
    }

    @Override
    public float getWeightFactor() {
        return weight;
    }

    @Override
    public float getWeight(MolangEnvironment environment) {
        float weight = isTransitioningIn() ?
                this.weight * getTransitionInProgress() :
                isTransitioningOut() ?
                        this.weight * getTransitionOutProgress() :
                        this.weight;

        if (weight == 0) return 0;

        environment.setThisValue(weight);
        return weight * environment.safeResolve(animation.blendWeight());
    }

    @Override
    public void setAnimationTime(float time) {
        if (paused) return;
        this.time = time;
    }

    @Override
    public void setWeight(float weight) {
        this.weight = weight;
    }

    @Override
    public boolean isDone() {
        return (getAnimation().loop() == AnimationData.Loop.NONE && getAnimationTime() >= getLength())
                || (stopped && (paused || !isTransitioningOut()));
    }

    public boolean isPlaying() {
        return !paused && !stopped;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public void stop() {
        if (!stopped) {
            stopped = true;
            stopTime = time;
        }
    }

    public boolean isStopped() {
        return stopped;
    }

    private float getAnimationDuration() {
        return stopped ? stopTime - startTime : animation.animationLength();
    }
}
