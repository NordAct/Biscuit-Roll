package nordmods.biscuit_roll.common.animation;

import gg.moonflower.molangcompiler.api.MolangEnvironment;
import gg.moonflower.pinwheel.api.animation.AnimationData;
import gg.moonflower.pinwheel.api.animation.PlayingAnimation;
import nordmods.biscuit_roll.common.animation.controller.BRAnimationController;

/// Animation that gets to play on {@link BRAnimationController}
public class BRPlayingAnimation implements PlayingAnimation {
    /// Animation data used for animation
    private final AnimationData animation;
    /// Animation running time. Cumulative
    private float time;
    /// Last animation render time
    private float lastRenderTime;
    /// Transition in time in seconds
    private final float transitionInTime;
    /// Transition in time out seconds
    private final float transitionOutTime;
    /// Transition in easing
    private final AnimationData.LerpMode transitionInLerp;
    /// Transition out easing
    private final AnimationData.LerpMode transitionOutLerp;
    /// Offset for {@link BRPlayingAnimation#getAnimationTime()}, useful when you want to set transition in point to different time in animation
    private final float startOffset;

    /// Animation speed
    private float speed = 1;
    /// Animation weight, affects how much effect transformations from animation have during animation process
    private float weight = 1;
    /// Marks if animation is finished
    private boolean finished = false;
    /// Marks if animation is currently paused
    private boolean paused = false;
    /// Timestamp of when animation finished, used for correct transition out
    private float finishTime;
    /// Used for correct transition out interpolation calculation case when animation got finished during transition in
    private float transitionInProgress = 1;
    public BRPlayingAnimation(AnimationData animation, float transitionInTime, float transitionOutTime, AnimationData.LerpMode transitionInLerp, AnimationData.LerpMode transitionOutLerp, float startOffset, float initialTimeOffset) {
        this.animation = animation;
        this.transitionInTime = Math.max(0, transitionInTime);
        this.transitionOutTime = Math.max(0, transitionOutTime);
        this.transitionInLerp = transitionInLerp;
        this.transitionOutLerp = transitionOutLerp;
        this.startOffset = startOffset;
        this.time = initialTimeOffset;
    }

    @Override
    public float getAnimationTime() {
        return (isTransitioningIn() ? 0 : finished ? Math.max(finishTime - transitionInTime, 0) : time - transitionInTime) + startOffset;
    }

    /// @return Animation's last render time
    public float getLastRenderAnimationTime() {
        return lastRenderTime;
    }

    /// @return Current transition in progress.
    /// Check if animation is actually transitioning in via {@link BRPlayingAnimation#isTransitioningIn()} before getting value from this method
    public float getTransitionInProgress() {
        return transitionInTime > 0 ? transitionInLerp.apply( time / transitionInTime) : 1;
    }

    /// @return Current transition out progress.
    /// Check if animation is actually transitioning out via {@link BRPlayingAnimation#isTransitioningOut()} before getting value from this method
    public float getTransitionOutProgress() {
        return transitionOutTime > 0 ?
                Math.max(transitionOutLerp.apply((transitionOutTime - (time - finishTime)) / transitionOutTime) - 1 + transitionInProgress, 0): 1;
    }

    /// @return if animation is currently transitioning in
    public boolean isTransitioningIn() {
        if (finished) return false;
        return time <= transitionInTime;
    }

    /// @return if animation is currently transitioning out
    public boolean isTransitioningOut() {
        if (!finished) return false;
        return time - finishTime <= transitionOutTime;
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

    /**
     * Adds provided time difference to current animation running time if it's not paused. {@link BRPlayingAnimation#speed} how much of this time difference will be added
     * @param timeDifference time difference between controller's previous animation time and current animation time
     */
    @Override
    public void setAnimationTime(float timeDifference) {
        if (paused) return;
        this.lastRenderTime = getRenderAnimationTime();
        this.time += timeDifference * speed;
    }


    @Override
    public void setWeight(float weight) {
        this.weight = weight;
    }

    @Override
    public boolean isDone() {
        return finished && (paused || !isTransitioningOut());
    }

    /// @return if animation can be cleared out of controller
    public boolean canClearOut() {
        return animation.loop() != AnimationData.Loop.HOLD_ON_LAST_FRAME;
    }

    /// @return if animation can continue running or should be finished
    public boolean canContinue() {
        return getAnimation().loop() == AnimationData.Loop.LOOP || getAnimationTime() < getLength();
    }

    /// @return if animation is currently running
    public boolean isRunning() {
        return !paused && !finished;
    }

    /// @return if animation is paused
    public boolean isPaused() {
        return paused;
    }

    /// Pauses or resumes animation
    /// @param paused animation pause state
    public void setPaused(boolean paused) {
        this.paused = paused;
        if (paused) lastRenderTime = getRenderAnimationTime();
    }

    /// Force finishes playing animation and begins transition out
    public void stop() {
        if (!finished) {
            finished = true;
            finishTime = time;
            lastRenderTime = getRenderAnimationTime();
            transitionInProgress = getTransitionInProgress();
        }
    }

    /// @return if animations is finished
    public boolean isFinished() {
        return finished;
    }

    /// @return animation speed
    public float getSpeed() {
        return speed;
    }

    /// Sets animation speed
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    @Override
    public float getRenderAnimationTime() {
        float animationTime = getAnimationTime();
        return switch (getAnimation().loop()) {
            case NONE -> animationTime;
            case LOOP -> getLength() > 0 ? animationTime % getLength() : animationTime;
            case HOLD_ON_LAST_FRAME -> Math.min(animationTime, getLength());
        };
    }

    /// @return transition in time
    public float getTransitionInTime() {
        return transitionInTime;
    }

    /// @return transition out time
    public float getTransitionOutTime() {
        return transitionOutTime;
    }

    /// @return transition in easing
    public AnimationData.LerpMode getTransitionInLerp() {
        return transitionInLerp;
    }

    /// @return transition out easing
    public AnimationData.LerpMode getTransitionOutLerp() {
        return transitionOutLerp;
    }
}
