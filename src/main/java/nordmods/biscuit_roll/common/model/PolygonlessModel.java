package nordmods.biscuit_roll.common.model;

import gg.moonflower.pinwheel.api.geometry.GeometryCompileException;
import gg.moonflower.pinwheel.api.geometry.GeometryModelData;
import gg.moonflower.pinwheel.api.geometry.GeometryTree;
import gg.moonflower.pinwheel.api.geometry.bone.AnimatedBone;
import gg.moonflower.pinwheel.api.transform.LocatorTransformation;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/// Model that contains only {@link PolygonlessAnimatedBone} and locators with no polygons to render. Used for {@link nordmods.biscuit_roll.common.animation.processor.AnimationProcessor}
public class PolygonlessModel extends BRModel {
    public PolygonlessModel(GeometryModelData model) throws GeometryCompileException {
        super(model);
    }

    @Override
    public GeometryTree createGeometryTree(GeometryModelData model) throws GeometryCompileException {
        return new PolygonlessGeometryTree(model.bones());
    }

    static class PolygonlessGeometryTree implements GeometryTree {
        private final Map<String, LocatorTransformation> locatorTransformations;
        private final GeometryModelData.Locator[] locators;
        private final Map<String, AnimatedBone> bones;
        private final List<AnimatedBone> bonesAsList;
        private final List<AnimatedBone> rootBones;

        public PolygonlessGeometryTree(GeometryModelData.Bone[] bones) throws GeometryCompileException {
            this.locatorTransformations = Arrays.stream(bones).flatMap(bone -> Arrays.stream(bone.locators()))
                    .map(LocatorTransformation::new)
                    .collect(Collectors.toMap(t -> t.locator().identifier(), t -> t));
            this.locators = Arrays.stream(bones).flatMap(bone -> Arrays.stream(bone.locators())).toArray(GeometryModelData.Locator[]::new);

            if (bones.length == 0) {
                this.bones = Collections.emptyMap();
                this.rootBones = List.of();
                this.bonesAsList = List.of();
                return;
            }

            Map<String, PolygonlessAnimatedBone.Builder> boneBuilders = Arrays.stream(bones).collect(Collectors.toMap(GeometryModelData.Bone::name, PolygonlessAnimatedBone::bone));
            Set<String> rootBones = new HashSet<>();

            List<GeometryModelData.Bone> unprocessedBones = new ArrayList<>(Arrays.asList(bones));
            while (!unprocessedBones.isEmpty()) {
                GeometryModelData.Bone bone = unprocessedBones.remove(0);
                String name = bone.name();
                String parent = bone.parent();

                if (parent == null) {
                    rootBones.add(name);
                    continue;
                }

                if (!boneBuilders.containsKey(parent)) {
                    throw new GeometryCompileException("Unknown bone: " + parent);
                }

                boneBuilders.get(parent).addChild(boneBuilders.get(name));
            }

            Set<AnimatedBone> compiledBones = new HashSet<>();
            List<AnimatedBone> listBones = new ArrayList<>(boneBuilders.size());
            for (String name : rootBones) {
                AnimatedBone bone = boneBuilders.get(name).create();
                compiledBones.add(bone);
                bone.listBones(listBones);
            }
            this.bones = listBones.stream().collect(Collectors.toUnmodifiableMap(bone -> bone.getBone().name(), bone -> bone));
            this.bonesAsList = List.copyOf(this.bones.values());
            this.rootBones = List.copyOf(compiledBones);
        }

        @Override
        public @Nullable AnimatedBone getBone(String name) {
            return this.bones.get(name);
        }

        @Override
        public List<AnimatedBone> getBones() {
            return this.bonesAsList;
        }

        @Override
        public List<AnimatedBone> getRootBones() {
            return this.rootBones;
        }

        @Override
        public @Nullable LocatorTransformation getLocatorTransformation(String name) {
            return this.locatorTransformations.get(name);
        }

        @Override
        public GeometryModelData.Locator[] getLocators() {
            return this.locators;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            PolygonlessGeometryTree that = (PolygonlessGeometryTree) o;
            return this.bones.equals(that.bones);
        }

        @Override
        public int hashCode() {
            return this.bones.hashCode();
        }

        @Override
        public String toString() {
            return "PolygonlessGeometryTree{bones=" + this.bones + "}";
        }
    }
}
