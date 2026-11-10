package nordmods.biscuit_roll.common.model;

import gg.moonflower.molangcompiler.api.MolangEnvironment;
import gg.moonflower.pinwheel.api.animation.AnimationData;
import gg.moonflower.pinwheel.api.animation.PlayingAnimation;
import gg.moonflower.pinwheel.api.geometry.*;
import gg.moonflower.pinwheel.api.geometry.bone.AnimatedBone;
import gg.moonflower.pinwheel.api.transform.LocatorTransformation;
import gg.moonflower.pinwheel.api.transform.MatrixStack;
import nordmods.biscuit_roll.common.animation.controller.BRAnimationController;
import nordmods.biscuit_roll.common.state.BRState;
import nordmods.biscuit_roll.common.state.StateDataTypes;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public abstract class BRModel implements GeometryModel {
    private final GeometryTree tree;
    public BRModel(GeometryModelData model) throws GeometryCompileException {
        this.tree = createGeometryTree(model);
    }

    public abstract GeometryTree createGeometryTree(GeometryModelData model) throws GeometryCompileException;

    @Override
    @ApiStatus.Internal
    public void render(GeometryRenderer renderer, MatrixStack matrixStack) {
        List<AnimatedBone> rootBones = tree.getRootBones();
        for (int i = 0, rootBonesSize = rootBones.size(); i < rootBonesSize; i++) {
            AnimatedBone bone = rootBones.get(i);
            bone.render(renderer, matrixStack);
        }
    }

    @Override
    public @Nullable AnimatedBone getBone(String name) {
        return tree.getBone(name);
    }

    @Override
    public List<AnimatedBone> getBones() {
        return tree.getBones();
    }

    @Override
    public List<AnimatedBone> getRootBones() {
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
    public void applyAnimations(MolangEnvironment environment, Collection<? extends PlayingAnimation> animations) {
        animations.forEach(animation -> {
            float blendWeight = animation.getWeight(environment);
            if (Math.abs(blendWeight) <= 1E-6) return;
            float localAnimationTime = animation.getRenderAnimationTime();
            environment.edit().setQuery("anim_time", localAnimationTime);
            for (AnimationData.BoneAnimation boneAnimation : animation.getAnimation().boneAnimations()) {
                AnimatedBone bone = this.getBone(boneAnimation.name());
                if (bone == null) continue;
                GeometryModel.applyKeyframeAnimation(localAnimationTime, blendWeight, environment, bone.getAnimationPose(), boneAnimation);
            }
        });
    }

    @ApiStatus.Internal
    public void applyAnimations(BRState state) {
        resetTransformation();
        List<BRAnimationController> stateData = state.getStateData(StateDataTypes.CONTROLLERS, List.of());
        for (int i = 0, stateDataSize = stateData.size(); i < stateDataSize; i++) {
            BRAnimationController controller = stateData.get(i);
            applyAnimations(controller.getEnvironment(), controller.getPlayingAnimations());
        }
        state.getStateData(StateDataTypes.ANIMATION_ADJUSTMENT, (_, _) -> {}).accept(state, this);
        updateLocators();
    }
}
