package nordmods.biscuit_roll.common.animation;

import gg.moonflower.molangcompiler.api.MolangEnvironment;
import gg.moonflower.molangcompiler.api.MolangRuntime;
import gg.moonflower.molangcompiler.api.exception.MolangRuntimeException;
import gg.moonflower.pinwheel.api.animation.AnimationController;
import gg.moonflower.pinwheel.api.animation.AnimationData;
import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.common.util.BRAnimationManager;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BRAnimationController implements AnimationController {
    private Map<String,BRPlayingAnimation> playingAnimations = new HashMap<>();
    private MolangEnvironment environment = MolangRuntime.runtime().create();
    private final Map<String, ProposedAnimationData> proposedAnimations = new HashMap<>();
    @Nullable private Identifier animationFile;
    private final boolean isClient;

    public BRAnimationController(BRAnimatedObject animatedObject) {
        this.isClient = animatedObject.isClient();
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
            int transitionInTime,
            AnimationData.LerpMode transitionOutEasing,
            int transitionOutTime
    ) {}

    public int getDefaultTransitionTime() {
        return 1;
    }

    public AnimationData.LerpMode getDefaultEasingType() {
        return AnimationData.LerpMode.LINEAR;
    }

    private AnimationData getAnimationData(String animation) {
        return BRAnimationManager.getAnimationManager(isClient).getAnimation(animationFile, animation);
    }
}
