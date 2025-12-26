package nordmods.biscuit_roll.common.animation;

import gg.moonflower.molangcompiler.api.MolangEnvironment;
import gg.moonflower.molangcompiler.api.MolangRuntime;
import gg.moonflower.molangcompiler.api.exception.MolangRuntimeException;
import gg.moonflower.pinwheel.api.animation.AnimationController;
import gg.moonflower.pinwheel.api.animation.AnimationData;
import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.common.model.BRModel;
import nordmods.biscuit_roll.common.state.BRState;
import nordmods.biscuit_roll.common.util.BRAnimationManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class BRAnimationController<O extends BRAnimatedObject> implements AnimationController {
    private Map<String,BRPlayingAnimation> playingAnimations = new HashMap<>();
    private MolangEnvironment environment = MolangRuntime.runtime().create();
    private final Map<String, ProposedAnimationData> proposedAnimations = new HashMap<>();
    @Nullable private Identifier animationFile;
    private final boolean isClient;
    private String currentAnimation;
    private final boolean singleAnimation;

    public BRAnimationController(O animatedObject, boolean isClient, boolean singleAnimation) {
        this.isClient = isClient;
        this.singleAnimation = singleAnimation;
    }

    @Override
    public void clearAnimations() {
        playingAnimations.clear();
        proposedAnimations.clear();
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
        if (animationFile != null) playQueuedAnimations();
        Map<String,BRPlayingAnimation> shouldContinue = new HashMap<>();
        playingAnimations.forEach((name, animation) -> {
            if (!animation.canContinue()) animation.stop();
            if (!animation.isDone() || !animation.canClearOut()) shouldContinue.put(name, animation);
        });
        playingAnimations = shouldContinue;
    }

    public void playQueuedAnimations() {
        proposedAnimations.forEach((animation, data) -> {
            try {
                if (singleAnimation) {
                    if (!animation.equals(currentAnimation)) return;
                    playingAnimations.forEach(((name, playingAnimation) -> {
                        if (!name.equals(currentAnimation)) playingAnimation.stop();
                    }));
                }

                float animationTime = getEnvironment().getQuery().get("anim_time").get(getEnvironment());
                if (playingAnimations.containsKey(animation)) return;

                AnimationData animationData = getAnimationData(animation);
                playingAnimations.put(animation, new BRPlayingAnimation(animationData, animationTime, data.transitionInTime, data.transitionOutTime, data.transitionInEasing, data.transitionOutEasing));
            }  catch (MolangRuntimeException e) {
                throw new RuntimeException("Couldn't find query \"anim_time\" in controller", e);
            }
        });
        proposedAnimations.clear();
    }

    public void playAnimation(String animation) {
        playAnimation(animation, new ProposedAnimationData(getDefaultEasingType(), getDefaultTransitionTime(), getDefaultEasingType(), getDefaultTransitionTime()));
    }

    public void playAnimation(String animation, ProposedAnimationData proposedAnimationData) {
        if (singleAnimation) {
            currentAnimation = animation;
        }
        proposedAnimations.put(animation, proposedAnimationData);
    }

    @Nullable
    public BRPlayingAnimation getAnimation(String animation) {
        return playingAnimations.get(animation);
    }

    public void setAnimationFile(@Nullable Identifier animationFile) {
        this.animationFile = animationFile;
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
        getPlayingAnimations().forEach(playingAnimation -> playingAnimation.setAnimationTime(time));
    }

    public void triggerAnimationEffects(@NotNull BRModel model, @NotNull BRState state) {
        getPlayingAnimations().forEach(playingAnimation -> {
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
