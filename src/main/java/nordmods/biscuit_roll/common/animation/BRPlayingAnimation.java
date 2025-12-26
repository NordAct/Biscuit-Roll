package nordmods.biscuit_roll.common.animation;

import gg.moonflower.molangcompiler.api.MolangEnvironment;
import gg.moonflower.pinwheel.api.animation.AnimationData;
import gg.moonflower.pinwheel.api.animation.PlayingAnimation;

@SuppressWarnings("unused")
public class BRPlayingAnimation implements PlayingAnimation {
    private final AnimationData animation;
    private float time;
    private float lastRenderTime;
    private final float startTime;
    private final float transitionInTime;
    private final float transitionOutTime;
    private final AnimationData.LerpMode transitionInLerp;
    private final AnimationData.LerpMode transitionOutLerp;
    private float speed = 1;
    private float weight = 1;
    private boolean stopped = false;
    private boolean paused = false;
    private float stopTime;
    private float transitionInProgressLeftover;
    public BRPlayingAnimation(AnimationData animation, float startTime, float transitionInTime, float transitionOutTime, AnimationData.LerpMode transitionInLerp, AnimationData.LerpMode transitionOutEasing) {
        this.animation = animation;
        this.startTime = startTime;
        this.transitionInTime = Math.max(0, transitionInTime);
        this.transitionOutTime = Math.max(0, transitionOutTime);
        this.transitionInLerp = transitionInLerp;
        this.transitionOutLerp = transitionOutEasing;
    }

    @Override
    public float getAnimationTime() {
        return isTransitioningIn() ? 0 : stopped ? stopTime - transitionInTime : time - transitionInTime;
    }

    public float getLastRenderAnimationTime() {
        return lastRenderTime;
    }

    public float getTransitionInProgress() {
        return transitionInTime > 0 ? transitionInLerp.apply(time / transitionInTime) : 1;
    }

    public float getTransitionOutProgress() {
        return transitionOutTime > 0 ?
                Math.max(transitionOutLerp.apply((transitionOutTime - (time - stopTime)) / transitionOutTime) - transitionInProgressLeftover, 0): 1;
    }

    public boolean isTransitioningIn() {
        if (stopped) return false;
        return time <= transitionInTime;
    }

    public boolean isTransitioningOut() {
        if (!stopped) return false;
        return time - stopTime <= transitionOutTime;
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
        this.lastRenderTime = getRenderAnimationTime();
        this.time = time - startTime * speed;
    }

    @Override
    public void setWeight(float weight) {
        this.weight = weight;
    }

    @Override
    public boolean isDone() {
        return stopped && (paused || !isTransitioningOut());
    }

    public boolean canClearOut() {
        return animation.loop() != AnimationData.Loop.HOLD_ON_LAST_FRAME;
    }

    public boolean canContinue() {
        return getAnimation().loop() == AnimationData.Loop.LOOP || getAnimationTime() < getLength();
    }

    public boolean isPlaying() {
        return !paused && !stopped;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
        if (paused) lastRenderTime = getRenderAnimationTime();
    }

    public void stop() {
        if (!stopped) {
            stopped = true;
            stopTime = time;
            transitionInProgressLeftover = 1 - getTransitionInProgress();
        }
    }

    public boolean isStopped() {
        return stopped;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}
