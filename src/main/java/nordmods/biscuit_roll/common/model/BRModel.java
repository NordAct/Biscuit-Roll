package nordmods.biscuit_roll.common.model;

import gg.moonflower.molangcompiler.api.MolangEnvironment;
import gg.moonflower.molangcompiler.api.exception.MolangRuntimeException;
import gg.moonflower.pinwheel.api.animation.AnimationData;
import gg.moonflower.pinwheel.api.animation.PlayingAnimation;
import gg.moonflower.pinwheel.api.geometry.*;
import gg.moonflower.pinwheel.api.geometry.bone.AnimatedBone;
import gg.moonflower.pinwheel.api.transform.LocatorTransformation;
import gg.moonflower.pinwheel.api.transform.MatrixStack;
import nordmods.biscuit_roll.common.animation.BRAnimationController;
import nordmods.biscuit_roll.common.state.BRState;
import nordmods.biscuit_roll.common.state.StateDataTypes;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BRModel implements GeometryModel {
    private final GeometryTree tree;

    public BRModel(GeometryModelData model) throws GeometryCompileException {
        this.tree = GeometryTree.create(model);
    }

    @Override
    @ApiStatus.Internal
    public void render(GeometryRenderer renderer, MatrixStack matrixStack) {
        for (AnimatedBone bone : tree.getRootBones()) {
            bone.render(renderer, matrixStack);
        }
    }

    @Override
    public @Nullable AnimatedBone getBone(String name) {
        return tree.getBone(name);
    }

    @Override
    public Collection<AnimatedBone> getBones() {
        return tree.getBones();
    }

    @Override
    public Collection<AnimatedBone> getRootBones() {
        return tree.getRootBones();
    }

    @Override
    public @Nullable LocatorTransformation getLocatorTransformation(String name) {
        return tree.getLocatorTransformation(name);
    }

    @Override
    public GeometryModelData.Locator[] getLocators() {
        return tree.getLocators();
    }

    @Override
    public void applyAnimations(MolangEnvironment environment, Collection<? extends PlayingAnimation> animations) {
        for (PlayingAnimation animation : animations) {
            float blendWeight = animation.getWeight(environment);
            if (Math.abs(blendWeight) <= 1E-6) {
                continue;
            }

            AnimationData data = animation.getAnimation();
            float localAnimationTime = animation.getRenderAnimationTime();
            for (AnimationData.BoneAnimation boneAnimation : data.boneAnimations()) {
                AnimatedBone bone = this.getBone(boneAnimation.name());
                if (bone == null) {
                    continue;
                }

                GeometryModel.applyKeyframeAnimation(localAnimationTime, blendWeight, environment, bone.getAnimationPose(), boneAnimation);
            }
        }
    }

    public void applyAnimations(BRState state) {
        Collection<BRAnimationController<?>> controllers = state.getStateDataOptional(StateDataTypes.CONTROLLERS).orElse(List.of());
        controllers.forEach(controller -> {
            try {
                float animationTime = controller.getEnvironment().getQuery().get("anim_time").get(controller.getEnvironment());
                controller.setAnimationFile(state.getStateData(StateDataTypes.MODEL_PROVIDER).getAnimationId(state));
                controller.setAnimationTime(animationTime);
                controller.tick();
            } catch (MolangRuntimeException e) {
                throw new RuntimeException("Couldn't find query \"anim_time\" in controller", e);
            }
        });
        resetTransformation();
        controllers.forEach(this::applyAnimations);
        updateLocators();
        controllers.forEach(controller -> controller.triggerAnimationEffects(this, state));
    }
}
