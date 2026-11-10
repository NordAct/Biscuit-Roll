package nordmods.biscuit_roll.common.model;

import gg.moonflower.pinwheel.api.geometry.GeometryCompileException;
import gg.moonflower.pinwheel.api.geometry.GeometryModelData;
import gg.moonflower.pinwheel.api.geometry.GeometryRenderer;
import gg.moonflower.pinwheel.api.geometry.LocatorAccess;
import gg.moonflower.pinwheel.api.geometry.bone.AnimatedBone;
import gg.moonflower.pinwheel.api.geometry.bone.ModelBone;
import gg.moonflower.pinwheel.api.geometry.bone.Polygon;
import gg.moonflower.pinwheel.api.transform.LocatorTransformation;
import gg.moonflower.pinwheel.api.transform.MatrixStack;
import org.joml.*;

import java.lang.Math;
import java.util.*;
import java.util.stream.Collectors;

/// {@link AnimatedBone} that does not have any polygon data
public class PolygonlessAnimatedBone implements AnimatedBone {
    private static final MatrixStack TRANSFORM_MATRIX = MatrixStack.create();
    private final GeometryModelData.Bone bone;
    private final List<AnimatedBone> children;
    private final Vector3f pivot;
    private final Vector3f rotation;
    private final Matrix4f copyPosition;
    private final Matrix3f copyNormal;
    private final AnimatedBone.AnimationPose animationPose;
    private boolean copyVanilla;
    private boolean visible;

    public PolygonlessAnimatedBone(GeometryModelData.Bone bone, List<AnimatedBone> children) {
        this.bone = bone;
        this.children = List.copyOf(children);
        this.pivot = new Vector3f();
        this.rotation = new Vector3f();
        this.copyPosition = new Matrix4f();
        this.copyNormal = new Matrix3f();
        this.animationPose = new AnimatedBone.AnimationPose();
        this.resetTransform();
        this.visible = true;
    }

    @Override
    public void resetTransform() {
        Vector3f rotation = this.bone.rotation();
        Vector3f pivot = this.bone.pivot();
        this.rotation.set(rotation).mul((float) (Math.PI / 180.0));
        this.pivot.set(pivot.x(), -pivot.y(), pivot.z());
        this.copyPosition.identity();
        this.copyNormal.identity();
        this.animationPose.identity();
        this.copyVanilla = false;
    }

    @Override
    public void render(GeometryRenderer renderer, MatrixStack matrixStack) {
        if (this.visible && (!this.children.isEmpty())) {
            matrixStack.pushMatrix();
            this.translateAndRotate(matrixStack);

            if (this.copyVanilla) {
                matrixStack.translate(-this.pivot.x() / 16.0F, -this.pivot.y() / 16.0F, -this.pivot.z() / 16.0F);
            }

            List<AnimatedBone> animatedBones = this.children;
            for (int i = 0, animatedBonesSize = animatedBones.size(); i < animatedBonesSize; i++) {
                AnimatedBone bone = animatedBones.get(i);
                bone.render(renderer, matrixStack);
            }

            matrixStack.popMatrix();
        }
    }

    @Override
    public void copyTransform(ModelBone bone) {
        this.copyPosition.identity();
        this.copyNormal.identity();
        TRANSFORM_MATRIX.position().identity();
        TRANSFORM_MATRIX.normal().identity();
        bone.translateAndRotate(TRANSFORM_MATRIX);
        this.copyPosition.mul(TRANSFORM_MATRIX.position());
        this.copyNormal.mul(TRANSFORM_MATRIX.normal());
        this.copyVanilla = !PolygonlessAnimatedBone.class.isAssignableFrom(bone.getClass());
    }

    @Override
    public void translateAndRotate(MatrixStack matrixStack) {
        Vector3fc pos = this.animationPose.position();
        Vector3fc rot = this.animationPose.rotation();
        Vector3fc scale = this.animationPose.scale();

        matrixStack.position().mul(this.copyPosition);
        matrixStack.normal().mul(this.copyNormal);
        matrixStack.translate((pos.x() + this.pivot.x()) / 16.0F, (-pos.y() + this.pivot.y()) / 16.0F, (pos.z() + this.pivot.z()) / 16.0F);
        matrixStack.rotateZYX(this.rotation.z() + (float) (rot.z() * Math.PI / 180.0F), this.rotation.y() + (float) (rot.y() * Math.PI / 180.0F), this.rotation.x() + (float) (rot.x() * Math.PI / 180.0F));
        matrixStack.scale(scale.x(), scale.y(), scale.z());
        matrixStack.translate(-this.pivot.x() / 16.0F, -this.pivot.y() / 16.0F, -this.pivot.z() / 16.0F);
    }

    @Override
    public GeometryModelData.Bone getBone() {
        return this.bone;
    }

    @Override
    public AnimationPose getAnimationPose() {
        return this.animationPose;
    }

    @Override
    public Collection<AnimatedBone> getChildren() {
        return this.children;
    }

    @Override
    public GeometryModelData.Locator[] getLocators() {
        return this.bone.locators();
    }

    @Override
    public void updateLocators(MatrixStack matrixStack, LocatorAccess access) {
        matrixStack.pushMatrix();
        this.translateAndRotate(matrixStack);

        for (GeometryModelData.Locator locator : this.bone.locators()) {
            String name = locator.identifier();
            LocatorTransformation transformation = access.getLocatorTransformation(name);
            if (transformation == null) {
                continue;
            }

            matrixStack.pushMatrix();
            Vector3fc pos = locator.position();
            matrixStack.translate(pos.x() / 16.0F, -pos.y() / 16.0F, pos.z() / 16.0F);
            transformation.matrix().set(matrixStack.position());
            matrixStack.popMatrix();
        }

        List<AnimatedBone> animatedBones = this.children;
        for (int i = 0, animatedBonesSize = animatedBones.size(); i < animatedBonesSize; i++) {
            AnimatedBone part = animatedBones.get(i);
            part.updateLocators(matrixStack, access);
        }

        matrixStack.popMatrix();
    }

    @Override
    public void listBones(Collection<AnimatedBone> bones) {
        bones.add(this);
        List<AnimatedBone> animatedBones = this.children;
        for (int i = 0, animatedBonesSize = animatedBones.size(); i < animatedBonesSize; i++) {
            AnimatedBone part = animatedBones.get(i);
            part.listBones(bones);
        }
    }

    @Override
    public void setVisible(boolean visible, boolean updateChildren) {
        this.visible = visible;
        if (updateChildren) {
            List<AnimatedBone> animatedBones = this.children;
            for (int i = 0, animatedBonesSize = animatedBones.size(); i < animatedBonesSize; i++) {
                AnimatedBone part = animatedBones.get(i);
                part.setVisible(visible);
            }
        }
    }

    @Override
    public boolean isVisible() {
        return this.visible;
    }

    @Override
    public Collection<Polygon> getPolygons() {
        return List.of();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PolygonlessAnimatedBone that = (PolygonlessAnimatedBone) o;
        return bone.equals(that.bone);
    }

    @Override
    public int hashCode() {
        return this.bone.hashCode();
    }

    @Override
    public String toString() {
        return "PolygonlessAnimatedBone{" +
                "name=" + this.bone.name() +
                ", children=" + this.children +
                '}';
    }

    static PolygonlessAnimatedBone.Builder bone(GeometryModelData.Bone bone) {
        return new PolygonlessAnimatedBone.Builder(bone);
    }

    static PolygonlessAnimatedBone.Builder bone(PolygonlessAnimatedBone.Builder copy) {
        return new PolygonlessAnimatedBone.Builder(copy);
    }

    static PolygonlessAnimatedBone create(GeometryModelData.Bone bone, List<AnimatedBone> children) {
        return new PolygonlessAnimatedBone(bone, children);
    }

    /// Same as {@link gg.moonflower.pinwheel.api.geometry.bone.AnimatedBone.Builder}, but for Polygonless Animated Bone
    public static class Builder {
        private final GeometryModelData.Bone bone;
        private final List<PolygonlessAnimatedBone.Builder> children;

        private Builder(GeometryModelData.Bone bone) {
            this.bone = bone;
            this.children = new ArrayList<>();
        }

        private Builder(PolygonlessAnimatedBone.Builder copy) {
            this.bone = copy.bone;
            this.children = copy.children.stream().map(PolygonlessAnimatedBone.Builder::new).collect(Collectors.toList());
        }

        public PolygonlessAnimatedBone.Builder addChild(GeometryModelData.Bone child) {
            return this.addChild(PolygonlessAnimatedBone.bone(child));
        }

        public PolygonlessAnimatedBone.Builder addChild(PolygonlessAnimatedBone.Builder child) {
            this.children.add(child);
            return this;
        }

        public GeometryModelData.Bone getBone() {
            return this.bone;
        }

        private void validate() throws GeometryCompileException {
            Set<GeometryModelData.Bone> checked = new HashSet<>();
            List<PolygonlessAnimatedBone.Builder> children = new ArrayList<>(this.children);
            while (!children.isEmpty()) {
                PolygonlessAnimatedBone.Builder child = children.remove(0);
                if (!checked.add(child.bone)) {
                    throw new GeometryCompileException("Circular reference in bone: " + child.bone);
                }

                children.addAll(child.children);
            }
        }

        private AnimatedBone createUnsafe() {
            List<AnimatedBone> children = new ArrayList<>(this.children.size());
            for (PolygonlessAnimatedBone.Builder child : this.children) {
                children.add(child.createUnsafe());
            }
            return PolygonlessAnimatedBone.create(this.bone, children);
        }

        public AnimatedBone create() throws GeometryCompileException {
            this.validate();
            return this.createUnsafe();
        }
    }
}
