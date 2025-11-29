package nordmods.biscuit_roll.common.model;

import gg.moonflower.pinwheel.api.geometry.*;
import gg.moonflower.pinwheel.api.geometry.bone.AnimatedBone;
import gg.moonflower.pinwheel.api.transform.LocatorTransformation;
import gg.moonflower.pinwheel.api.transform.MatrixStack;
import nordmods.biscuit_roll.common.state.BRState;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BRModel<S extends BRState> implements GeometryModel {
    //private final Map<String, AnimatedBone.AnimationPose> boneTransformations;
    private final GeometryTree tree;

    public BRModel(GeometryModelData model) throws GeometryCompileException {
        this.tree = GeometryTree.create(model);
        //this.boneTransformations = this.tree.getBones()
        //        .stream()
        //        .collect(Collectors.toUnmodifiableMap(bone -> bone.getBone().name(), unused -> new AnimatedBone.AnimationPose()));
    }

    @Override
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

    public void animate(S state) {
    }
}
