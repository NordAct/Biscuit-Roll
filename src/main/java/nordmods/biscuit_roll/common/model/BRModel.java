package nordmods.biscuit_roll.common.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import gg.moonflower.molangcompiler.api.MolangEnvironment;
import gg.moonflower.pinwheel.api.animation.AnimationData;
import gg.moonflower.pinwheel.api.animation.PlayingAnimation;
import gg.moonflower.pinwheel.api.geometry.*;
import gg.moonflower.pinwheel.api.geometry.bone.AnimatedBone;
import gg.moonflower.pinwheel.api.geometry.bone.Polygon;
import gg.moonflower.pinwheel.api.geometry.bone.Vertex;
import gg.moonflower.pinwheel.api.transform.LocatorTransformation;
import gg.moonflower.pinwheel.api.transform.MatrixStack;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import nordmods.biscuit_roll.client.internal.BRModelRenderer;
import nordmods.biscuit_roll.client.state.ClientStateDataTypes;
import nordmods.biscuit_roll.client.util.ClientAnimationManager;
import nordmods.biscuit_roll.common.animation.BRAnimationController;
import nordmods.biscuit_roll.common.state.BRState;
import nordmods.biscuit_roll.common.state.StateDataTypes;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.*;

public class BRModel implements GeometryModel {
    private final GeometryTree tree;

    public BRModel(GeometryModelData model) throws GeometryCompileException {
        this.tree = GeometryTree.create(model);
    }

    @Override
    public void render(GeometryRenderer renderer, MatrixStack matrixStack) {
        for (AnimatedBone bone : tree.getRootBones()) {
            bone.render(renderer, matrixStack);
        }
    }

    public void render(PoseStack stack, BRState state, VertexConsumer vertexConsumer) {
        render((matrixStack, polygon) -> renderPolygon(stack.last(), polygon, vertexConsumer, state), stack);
    }

    private void renderPolygon(PoseStack.Pose pose, Polygon polygon, VertexConsumer vertexConsumer, BRState state) {
        Matrix4f matrix4f = pose.pose();
        Vector3f vector3f = new Vector3f();

        for (int i = 0; i < 4; i ++) {
            Vector3f normal = pose.transformNormal(polygon.normals()[i], vector3f);
            float normalX = normal.x();
            float normalY = normal.y();
            float normalZ = normal.z();

            Vertex vertex = polygon.vertices()[i];
            float vertexX = vertex.x();
            float vertexY = vertex.y();
            float vertexZ = vertex.z();

            Vector3f pos = matrix4f.transformPosition(vertexX, vertexY, vertexZ, vector3f);

            vertexConsumer.addVertex(
                    pos.x(), pos.y(), pos.z(),
                    state.getStateData(ClientStateDataTypes.COLOR).orElse(-1),
                    vertex.u(), vertex.v(),
                    state.getStateData(ClientStateDataTypes.OVERLAY_TEXTURE).orElse(OverlayTexture.NO_OVERLAY),
                    state.getStateData(ClientStateDataTypes.LIGHT).orElse(LightTexture.FULL_BRIGHT),
                    normalX, normalY, normalZ);
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
        Collection<BRAnimationController> controllers = state.getStateData(StateDataTypes.CONTROLLERS).orElse(List.of());
        float animationTime = state.getStateData(StateDataTypes.ANIMATION_TIME).orElse(0f);
        controllers.forEach(controller -> {
            controller.playQueuedAnimations(state, animationTime);
            controller.setAnimationTime(animationTime);
            controller.tick();
        });
        resetTransformation();
        controllers.forEach(this::applyAnimations);
    }
}
