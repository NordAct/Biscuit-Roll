package nordmods.biscuit_roll.common.model;

import gg.moonflower.molangcompiler.api.MolangEnvironment;
import gg.moonflower.pinwheel.api.animation.AnimationController;
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
import org.joml.Vector3f;

import java.util.*;

/// Model to be animated and rendered
/// @see nordmods.biscuit_roll.common.util.BRModelManager
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
    @ApiStatus.Internal
    public void applyAnimations(AnimationController controller) {
        GeometryModel.super.applyAnimations(controller);
    }

    @Override
    @ApiStatus.Internal
    public void applyAnimations(MolangEnvironment environment, Collection<? extends PlayingAnimation> animations) {
        for (PlayingAnimation animation : animations) {
            float blendWeight = animation.getWeight(environment);
            if (Math.abs(blendWeight) <= 1E-6) {
                continue;
            }

            AnimationData data = animation.getAnimation();
            float localAnimationTime = animation.getRenderAnimationTime();
            environment.edit().setQuery("anim_time", localAnimationTime);

            for (AnimationData.BoneAnimation boneAnimation : data.boneAnimations()) {
                AnimatedBone bone = this.getBone(boneAnimation.name());
                if (bone == null) {
                    continue;
                }

                GeometryModel.applyKeyframeAnimation(localAnimationTime, blendWeight, environment, bone.getAnimationPose(), boneAnimation);
            }
        }
    }

    @ApiStatus.Internal
    public void applyAnimations(BRState state) {
        Collection<BRAnimationController> controllers = state.getStateData(StateDataTypes.CONTROLLERS);
        resetTransformation();
        controllers.forEach(this::applyAnimations);
        updateLocators();
    }

    @ApiStatus.Internal
    public void applyAnimationsFromStorage(BRState state) {
        Map<String, AnimatedBone.AnimationPose> poseStorage = state.getStateDataOptional(StateDataTypes.POSE_STORAGE).orElse(Map.of());
        resetTransformation();
        poseStorage.forEach((bone, pose) -> {
            AnimatedBone animatedBone = this.getBone(bone);
            if (animatedBone == null) return;
            animatedBone.getAnimationPose().position().x = pose.position().x();
            animatedBone.getAnimationPose().position().y = pose.position().y();
            animatedBone.getAnimationPose().position().z = pose.position().z();
            animatedBone.getAnimationPose().rotation().x = pose.rotation().x();
            animatedBone.getAnimationPose().rotation().y = pose.rotation().y();
            animatedBone.getAnimationPose().rotation().z = pose.rotation().z();
            animatedBone.getAnimationPose().scale().x = pose.scale().x();
            animatedBone.getAnimationPose().scale().y = pose.scale().y();
            animatedBone.getAnimationPose().scale().z = pose.scale().z();
        });
        updateLocators();
    }
}
