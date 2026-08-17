package nordmods.biscuit_roll.common.model;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import gg.moonflower.molangcompiler.api.MolangEnvironment;
import gg.moonflower.pinwheel.api.animation.AnimationData;
import gg.moonflower.pinwheel.api.animation.PlayingAnimation;
import gg.moonflower.pinwheel.api.geometry.*;
import gg.moonflower.pinwheel.api.geometry.bone.AnimatedBone;
import gg.moonflower.pinwheel.api.geometry.bone.Polygon;
import gg.moonflower.pinwheel.api.geometry.bone.Vertex;
import gg.moonflower.pinwheel.api.transform.LocatorTransformation;
import gg.moonflower.pinwheel.api.transform.MatrixStack;
import net.minecraft.util.Util;
import nordmods.biscuit_roll.common.animation.controller.BRAnimationController;
import nordmods.biscuit_roll.common.resource_managers.BRModelManager;
import nordmods.biscuit_roll.common.state.BRState;
import nordmods.biscuit_roll.common.state.StateDataTypes;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.jspecify.annotations.Nullable;

import java.util.*;

/// Model to be animated and rendered
/// @see BRModelManager
public class BRModel implements GeometryModel {
    private final GeometryTree tree;
    private final Map<String, List<Pair<String, Vector3f>>> polymeshPositions;
    public static final Collection<BRAnimationController> EMPTY_CONTROLLER_COLLECTION = List.of();

    public BRModel(GeometryModelData model, @Nullable PolyMeshAttachments polyMeshAttachments) throws GeometryCompileException {
        this.tree = GeometryTree.create(model);
        polymeshPositions = ImmutableMap.copyOf(polyMeshAttachments == null ? Map.of() : Util.make(new HashMap<>(), map -> {
            polyMeshAttachments.attachments().forEach((bone, positions) -> {
                AnimatedBone animatedBone = getBone(bone);
                if (animatedBone == null) return;
                List<Pair<String, Vector3f>> attachments = new LinkedList<>();
                positions.forEach((locator, initialPos) -> {
                    Vector3f converted = new Vector3f(initialPos.x() * 0.0625f, -initialPos.y() * 0.0625f, initialPos.z() * 0.0625f);
                    for (Polygon polygon : animatedBone.getPolygons()) {
                        if (polygon.material() == null) continue;
                        if (!polygon.material().startsWith("poly_mesh")) continue;
                        for (Vertex vertex : polygon.vertices()) {
                            if (converted.equals(vertex.pos())) {
                                attachments.add(new Pair<>(locator, vertex.pos()));
                            }
                        }
                    }
                });
                map.put(bone, Collections.unmodifiableList(attachments));
            });
        }));
    }

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
    public void updateLocators() {
        GeometryModel.super.updateLocators();
        polymeshPositions.forEach((_, attachments) -> attachments.forEach(pair -> {
            LocatorTransformation transformation = getLocatorTransformation(pair.getFirst());
            if (transformation != null) {
                Vector4f pos = transformation.matrix().transform(new Vector4f(0, 0, 0, 1));
                pair.getSecond().set(-pos.x(), -pos.y(), pos.z());
            }
        }));
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
                if (bone == null) return;
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
