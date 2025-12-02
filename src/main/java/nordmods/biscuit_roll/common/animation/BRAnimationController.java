package nordmods.biscuit_roll.common.animation;

import gg.moonflower.molangcompiler.api.MolangEnvironment;
import gg.moonflower.molangcompiler.api.MolangRuntime;
import gg.moonflower.pinwheel.api.animation.AnimationController;
import gg.moonflower.pinwheel.api.animation.AnimationData;
import gg.moonflower.pinwheel.api.animation.PlayingAnimation;
import nordmods.biscuit_roll.common.model.BRModelProvider;
import nordmods.biscuit_roll.common.state.BRState;
import nordmods.biscuit_roll.common.state.StateDataTypes;
import nordmods.biscuit_roll.common.util.BRAnimationManager;

import java.util.*;

public class BRAnimationController implements AnimationController {
    private final Set<BRPlayingAnimation> animations = new HashSet<>();
    private final MolangEnvironment environment;
    private final Set<String> proposedAnimations = new HashSet<>();
    private final BRAnimatedObject animatedObject;

    public BRAnimationController(BRAnimatedObject animatedObject) {
        this.animatedObject = animatedObject;
        environment = MolangRuntime.runtime().setVariables(animatedObject).create();
    }

    @Override
    public void clearAnimations() {
        getPlayingAnimations() .clear();
        proposedAnimations.clear();
    }

    @Override
    public MolangEnvironment getEnvironment() {
        return environment;
    }

    @Override
    public Collection<BRPlayingAnimation> getPlayingAnimations() {
        return animations;
    }

    public void tick() {
        getPlayingAnimations().removeIf(PlayingAnimation::isDone);
    }

    public <S extends BRState> void playQueuedAnimations(S state, float animationTime) {
        BRModelProvider<S> modelProvider = (BRModelProvider<S>) state.getStateData(StateDataTypes.MODEL_PROVIDER).orElse(null);
        if (modelProvider == null) return;
        proposedAnimations.forEach(animation -> {
            if (getPlayingAnimations().stream().anyMatch(playing -> playing.getAnimation().name().equals(animation))) return;
            AnimationData data = BRAnimationManager.getAnimationManager(animatedObject.isClient()).getAnimation(modelProvider.getAnimationId(state), animation);
            getPlayingAnimations().add(new BRPlayingAnimation(data, animationTime));
        });
        proposedAnimations.clear();
    }

    public void playAnimation(String animation) {
        proposedAnimations.add(animation);
    }

    public void stopAnimation(String animation) {
        getPlayingAnimations().removeIf(playing -> playing.getAnimation().name().equals(animation));
    }
}
