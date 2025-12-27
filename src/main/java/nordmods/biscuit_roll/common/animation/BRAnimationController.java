package nordmods.biscuit_roll.common.animation;

import gg.moonflower.molangcompiler.api.MolangEnvironment;
import gg.moonflower.molangcompiler.api.MolangRuntime;
import gg.moonflower.pinwheel.api.animation.AnimationController;
import gg.moonflower.pinwheel.api.animation.AnimationData;
import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.common.model.BRModel;
import nordmods.biscuit_roll.common.state.BRState;
import nordmods.biscuit_roll.common.util.BRAnimationManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BRAnimationController implements AnimationController {
    protected final ConcurrentHashMap<String,BRPlayingAnimation> playingAnimations = new ConcurrentHashMap<>();
    private final MolangEnvironment environment = MolangRuntime.runtime().create();
    @Nullable protected Identifier animationFile;
    protected final boolean isClient;
    protected final boolean singleAnimation;
    private float animationTime = 0;

    public BRAnimationController(boolean isClient, boolean singleAnimation) {
        this.isClient = isClient;
        this.singleAnimation = singleAnimation;
    }

    @Override
    public void clearAnimations() {
        playingAnimations.clear();
    }

    @Override
    public MolangEnvironment getEnvironment() {
        return environment;
    }

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

    public void playAnimation(String animation) {
        playAnimation(animation, new ProposedAnimationData(getDefaultEasingType(), getDefaultTransitionTime(), getDefaultEasingType(), getDefaultTransitionTime()));
    }

    public void playAnimation(String animation, ProposedAnimationData proposedAnimationData) {
        if (animationFile == null) return;

        if (singleAnimation) {
            playingAnimations.forEach(((name, playingAnimation) -> {
                if (!name.equals(animation)) playingAnimation.stop();
            }));
        }

        float animationTime = this.animationTime;
        if (playingAnimations.containsKey(animation)) {
            BRPlayingAnimation playingAnimation = playingAnimations.get(animation);
            if (playingAnimation.isTransitioningOut() && playingAnimation.canContinue()) {
                float transitionOutProgress = playingAnimation.getTransitionOutProgress();
                animationTime -= proposedAnimationData.transitionInTime * transitionOutProgress;
            } else return;
        }
        AnimationData animationData = getAnimationData(animation);
        playingAnimations.put(animation, new BRPlayingAnimation(animationData, animationTime, proposedAnimationData.transitionInTime, proposedAnimationData.transitionOutTime, proposedAnimationData.transitionInEasing, proposedAnimationData.transitionOutEasing));
    }

    @Nullable
    public BRPlayingAnimation getAnimation(String animation) {
        return playingAnimations.get(animation);
    }

    public void setAnimationFile(@Nullable Identifier animationFile) {
        this.animationFile = animationFile;
    }

    public float getAnimationTime() {
        return animationTime;
    }

    public record ProposedAnimationData(
            AnimationData.LerpMode transitionInEasing,
            float transitionInTime,
            AnimationData.LerpMode transitionOutEasing,
            float transitionOutTime
    ) {}

    public float getDefaultTransitionTime() {
        return 1;
    }

    public AnimationData.LerpMode getDefaultEasingType() {
        return AnimationData.LerpMode.LINEAR;
    }

    @Override
    public void setAnimationTime(float time) {
        this.animationTime = time;
        getPlayingAnimations().forEach(playingAnimation -> playingAnimation.setAnimationTime(time));
    }

    public void triggerAnimationEffects(@NotNull BRModel model, @NotNull BRState state) {
        getPlayingAnimations().forEach(playingAnimation -> {
            if (!playingAnimation.isPlaying()) return;

            float lastTime = playingAnimation.getLastRenderAnimationTime();
            float newTime = playingAnimation.getRenderAnimationTime();

            for (AnimationData.SoundEffect soundEffect : playingAnimation.getAnimation().soundEffects()) {
                float triggerTime = soundEffect.time();
                if ((triggerTime > lastTime || lastTime > newTime) && triggerTime <= newTime)
                    onSoundEffect(soundEffect, model, state);
            }

            for (AnimationData.ParticleEffect particleEffect : playingAnimation.getAnimation().particleEffects()) {
                float triggerTime = particleEffect.time();
                if ((triggerTime > lastTime || lastTime > newTime) && triggerTime <= newTime) {
                    onParticleEffect(particleEffect, model, state);
                }
            }

            for (AnimationData.TimelineEffect timelineEffect : playingAnimation.getAnimation().timelineEffects()) {
                float triggerTime = timelineEffect.time();
                if ((triggerTime > lastTime || lastTime > newTime) && triggerTime <= newTime)
                    onTimelineEffect(timelineEffect, model, state);
            }
        });
    }

    protected abstract void onSoundEffect(AnimationData.SoundEffect soundEffect, BRModel model, BRState state);

    protected abstract void onParticleEffect(AnimationData.ParticleEffect particleEffect, BRModel model, BRState state);

    protected abstract void onTimelineEffect(AnimationData.TimelineEffect timelineEffect, BRModel model, BRState state);

    private AnimationData getAnimationData(String animation) {
        return BRAnimationManager.getAnimationManager(isClient).getAnimation(animationFile, animation);
    }
}
