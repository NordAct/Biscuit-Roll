package nordmods.biscuit_roll.common.animation;

import gg.moonflower.pinwheel.api.animation.AnimationData;
import nordmods.biscuit_roll.common.model.BRModel;
import nordmods.biscuit_roll.common.state.BRState;

import java.util.Collection;

public class CloneAnimationController extends BRAnimationController{
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
    public void tick() {}

    @Override
    public void playAnimation(String animation) {}

    @Override
    public void playAnimation(String animation, ProposedAnimationData proposedAnimationData) {}

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
