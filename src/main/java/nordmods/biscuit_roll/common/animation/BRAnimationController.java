package nordmods.biscuit_roll.common.animation;

import gg.moonflower.molangcompiler.api.MolangEnvironment;
import gg.moonflower.molangcompiler.api.MolangEnvironmentBuilder;
import gg.moonflower.molangcompiler.api.MolangRuntime;
import gg.moonflower.molangcompiler.api.exception.MolangRuntimeException;
import gg.moonflower.pinwheel.api.animation.AnimationController;
import gg.moonflower.pinwheel.api.animation.AnimationData;
import net.minecraft.util.EasingType;
import nordmods.biscuit_roll.common.model.BRModelProvider;
import nordmods.biscuit_roll.common.state.BRState;
import nordmods.biscuit_roll.common.state.StateDataTypes;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BRAnimationController implements AnimationController {
    private final Map<String,BRPlayingAnimation> playingAnimations = new HashMap<>();
    private MolangEnvironment environment = MolangRuntime.runtime().create();;
    private final Map<String, ProposedAnimationData> proposedAnimations = new HashMap<>();
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
        playingAnimations.forEach((name, animation) -> {
            if (animation.isDone()) playingAnimations.remove(name);
        });
    }

    public <S extends BRState> void playQueuedAnimations(S state, float animationTime) {
        BRModelProvider modelProvider = state.getStateData(StateDataTypes.MODEL_PROVIDER);
        if (modelProvider == null) return;
        proposedAnimations.forEach((animation, data) -> {
            if (playingAnimations.containsKey(animation)) return;
            AnimationData animationData = modelProvider.getAnimationData(state, isClient, animation);
            playingAnimations.put(animation, new BRPlayingAnimation(animationData, animationTime, data.transitionInTime, data.transitionOutTime, data.transitionInEasing, data.transitionOutEasing));
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

    public record ProposedAnimationData(
            EasingType transitionInEasing,
            int transitionInTime,
            EasingType transitionOutEasing,
            int transitionOutTime
    ) {}

    public int getDefaultTransitionTime() {
        return 1;
    }

    public EasingType getDefaultEasingType() {
        return EasingType.LINEAR;
    }

    public void updateMolangEnvironment(MolangEnvironmentUpdateProvider ... updateProviders) {
        if (!environment.canEdit()) return;
        MolangEnvironmentBuilder<?> builder = environment.edit();
        for (MolangEnvironmentUpdateProvider updateProvider : updateProviders) updateProvider.update(builder);
        environment = builder.create();
    }

    public interface MolangEnvironmentUpdateProvider {
        void update(MolangEnvironmentBuilder<?> environmentBuilder);
    }
}
