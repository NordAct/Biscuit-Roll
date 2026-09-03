package nordmods.biscuit_roll.common.animation.controller;

import gg.moonflower.molangcompiler.api.MolangEnvironment;
import gg.moonflower.molangcompiler.api.MolangRuntime;
import gg.moonflower.pinwheel.api.animation.AnimationController;
import gg.moonflower.pinwheel.api.animation.AnimationData;
import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.common.animation.BRPlayingAnimation;
import nordmods.biscuit_roll.common.model.BRModel;
import nordmods.biscuit_roll.common.state.BRState;
import nordmods.biscuit_roll.common.state.StateDataTypes;
import nordmods.biscuit_roll.common.resource_managers.BRAnimationManager;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/// Biscuit Roll Animation Controller or BRAnimationController for short
///
/// Responsible for managing playing animations
public abstract class BRAnimationController implements AnimationController {
    protected final ConcurrentHashMap<String, BRPlayingAnimation> playingAnimations = new ConcurrentHashMap<>();
    /// Molang environment that is used for resolving Molang expressions
    private final MolangEnvironment environment = MolangRuntime.runtime().create();
    /// Current animation file
    @Nullable
    protected Identifier animationFile;
    /// Defines if controller can have only one animation running at the time.
    /// If it is, when new animation is attempted to be played, previously running animation will be stopped via {@link BRPlayingAnimation#stop()}
    public final boolean singleAnimation;
    /// Controller's current animation time
    private float animationTime = 0;

    /// @param singleAnimation is controller in single animation mode
    public BRAnimationController(boolean singleAnimation) {
        this.singleAnimation = singleAnimation;
    }

    /// Clears out all animations from controller
    @Override
    public void clearAnimations() {
        playingAnimations.clear();
    }

    /// @return controller's {@link MolangEnvironment}
    @Override
    public MolangEnvironment getEnvironment() {
        return environment;
    }

    /// @return all currently playing animations
    @Override
    public Collection<BRPlayingAnimation> getPlayingAnimations() {
        return playingAnimations.values();
    }

    public void tick() {
        Map<String,BRPlayingAnimation> shouldContinue = new HashMap<>();
        playingAnimations.forEach((name, animation) -> {
            if (!animation.canContinue()) animation.stop();
            if (!animation.isDone() || !animation.canClearOut()) shouldContinue.put(name, animation);
        });
        playingAnimations.clear();
        playingAnimations.putAll(shouldContinue);
    }

    /// Plays animation by specified name with default transition time ({@link BRAnimationController#getDefaultTransitionTime()}) and in-out easing ({@link BRAnimationController#getDefaultEasingType()})
    /// @param animation animation name
    public void playAnimation(String animation) {
        playAnimation(animation, getDefaultTransitionTime(), getDefaultTransitionTime(), getDefaultEasingType(), getDefaultEasingType());
    }

    /// @param animation animation name
    /// @param transitionInTime animation transition in time
    /// @param transitionOutTime animation transition out time
    /// @param transitionInLerp animation transition in easing
    /// @param transitionOutLerp animation transition out easing
    public void playAnimation(String animation, float transitionInTime, float transitionOutTime, AnimationData.LerpMode transitionInLerp, AnimationData.LerpMode transitionOutLerp) {
        if (singleAnimation) {
            playingAnimations.forEach(((name, playingAnimation) -> {
                if (!playingAnimation.isFinished() && !name.equals(animation)) playingAnimation.stop();
            }));
        }

        AnimationData data = getAnimationData(animation);
        if (data == null) return;

        if (playingAnimations.containsKey(animation)) {
            BRPlayingAnimation playingAnimation = playingAnimations.get(animation);
            if (playingAnimation.isTransitioningOut() && playingAnimation.canContinue()) {
                BRPlayingAnimation resumedAnimation = new BRPlayingAnimation(
                        data,
                        transitionInTime,
                        transitionOutTime,
                        transitionInLerp,
                        transitionOutLerp,
                        playingAnimation.getRenderAnimationTime()
                );
                resumedAnimation.setAnimationTime(transitionInTime * transitionInLerp.apply(playingAnimation.getTransitionOutProgress()));
                playAnimation(resumedAnimation);
            }
            return;
        }

        playAnimation(new BRPlayingAnimation(data, transitionInTime, transitionOutTime, transitionInLerp, transitionOutLerp, 0));
    }

    /// Adds [BRPlayingAnimation] directly to the collection of playing animations. May override already playing animation if animation with same name is already playing
    /// @param animation animation to add
    public void playAnimation(BRPlayingAnimation animation) {
        if (singleAnimation) {
            playingAnimations.forEach(((name, playingAnimation) -> {
                if (!playingAnimation.isFinished()) playingAnimation.stop();
            }));
        }
        playingAnimations.put(animation.getAnimation().name(), animation);
    }

    /// @param animation animation name
    /// @return animation of specified name if it's playing on this controller, null if not
    @Nullable
    public BRPlayingAnimation getAnimation(String animation) {
        return playingAnimations.get(animation);
    }

    /// @return controller's animation time
    public float getAnimationTime() {
        return animationTime;
    }

    /// @return default animation transition time in seconds
    public float getDefaultTransitionTime() {
        return 1;
    }

    /// @return default easing to be used in animation transitions
    //todo figure out why some lerp modes cause twitching when they shouldn't
    @ApiStatus.Experimental
    public AnimationData.LerpMode getDefaultEasingType() {
        return AnimationData.LerpMode.LINEAR;
    }

    /// Sets controller's animation time and updates time for all playing animations
    /// @param time The new time in seconds
    @Override
    public void setAnimationTime(float time) {
        float previousAnimationTime = this.animationTime;
        this.animationTime = time;
        float diff = animationTime - previousAnimationTime;
        getPlayingAnimations().forEach(playingAnimation -> playingAnimation.advanceAnimationTime(diff));
    }

    /// Triggers effect keyframes on playing animations
    /// @param model animated model
    /// @param state animated model state
    public void triggerAnimationEffects(@NonNull BRModel model, @NonNull BRState state) {
        getPlayingAnimations().forEach(playingAnimation -> {
            if (!playingAnimation.isRunning() || playingAnimation.isTransitioningIn()) return;

            float lastTime = playingAnimation.getLastRenderAnimationTime();
            float newTime = playingAnimation.getRenderAnimationTime();

            if (lastTime == newTime) return;

            for (AnimationData.SoundEffect soundEffect : playingAnimation.getAnimation().soundEffects()) {
                float triggerTime = soundEffect.time();
                if ((triggerTime >= lastTime || lastTime >= newTime) && (triggerTime < newTime || triggerTime == newTime && newTime == playingAnimation.getAnimation().animationLength()))
                    onSoundEffect(soundEffect, model, state);
            }

            for (AnimationData.ParticleEffect particleEffect : playingAnimation.getAnimation().particleEffects()) {
                float triggerTime = particleEffect.time();
                if ((triggerTime >= lastTime || lastTime >= newTime) && (triggerTime < newTime || triggerTime == newTime && newTime == playingAnimation.getAnimation().animationLength())) {
                    onParticleEffect(particleEffect, model, state);
                }
            }

            for (AnimationData.TimelineEffect timelineEffect : playingAnimation.getAnimation().timelineEffects()) {
                float triggerTime = timelineEffect.time();
                if ((triggerTime >= lastTime || lastTime > newTime) && (triggerTime < newTime || triggerTime == newTime && newTime == playingAnimation.getAnimation().animationLength()))
                    onTimelineEffect(timelineEffect, model, state);
            }
        });
    }

    /// Updates animation file id and animation time from data provided in [BRState] state
    /// @param state provided state
    public void update(BRState state) {
        animationFile = state.getStateData(StateDataTypes.MODEL_PROVIDER).getAnimationId(state);
        float animationTime = state.getStateData(StateDataTypes.ANIMATION_TIME, 0f);
        setAnimationTime(animationTime);
        tick();
    }

    /// Defines behavior of sound effect keyframe
    /// @param soundEffect sound effect keyframe data
    /// @param model animated model
    /// @param state animated model state
    protected abstract void onSoundEffect(AnimationData.SoundEffect soundEffect, BRModel model, BRState state);

    /// Defines behavior of particle effect keyframe
    /// @param particleEffect particle effect keyframe data
    /// @param model animated model
    /// @param state animated model state
    protected abstract void onParticleEffect(AnimationData.ParticleEffect particleEffect, BRModel model, BRState state);

    /// Defines behavior of timeline effect keyframe
    /// @param timelineEffect timeline effect keyframe data
    /// @param model animated model
    /// @param state animated model state
    protected abstract void onTimelineEffect(AnimationData.TimelineEffect timelineEffect, BRModel model, BRState state);

    /// Gets {@link AnimationData} for animation to play from current {@link BRAnimationController#animationFile}
    /// @param animation animation name
    /// @return AnimationData for specified animation
    @Nullable
    public AnimationData getAnimationData(String animation) {
        return animationFile == null ? null : BRAnimationManager.getAnimationManager(true).getAnimation(animationFile, animation);
    }
}
