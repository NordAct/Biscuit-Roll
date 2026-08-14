package nordmods.biscuit_roll.common.util;

import gg.moonflower.molangcompiler.api.MolangExpression;
import gg.moonflower.molangcompiler.api.bridge.MolangVariable;
import gg.moonflower.molangcompiler.api.exception.MolangSyntaxException;
import gg.moonflower.molangcompiler.impl.node.MolangCompoundNode;
import gg.moonflower.molangcompiler.impl.node.MolangConstantNode;
import gg.moonflower.molangcompiler.impl.node.MolangVariableNode;
import gg.moonflower.pinwheel.api.PinwheelMolangCompiler;
import gg.moonflower.pinwheel.api.animation.AnimationData;
import gg.moonflower.pinwheel.api.geometry.GeometryModelData;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import nordmods.biscuit_roll.BiscuitRoll;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.Optional;

public class BRStreamCodecs {
    private static final byte LIKELY_COMPLEX_EXPRESSION_ID = -1;
    public static final StreamCodec<ByteBuf, MolangExpression> MOLANG_EXPRESSION = new StreamCodec<>() { //todo think about better looking solution
        @Override
        public MolangExpression decode(ByteBuf input) {
            byte id = input.readByte();
            return switch (id) {
                case 0 -> new MolangVariableNode(MolangVariable.create(input.readFloat()));
                case 1 -> new MolangConstantNode(input.readFloat());
                case 2 -> {
                    int length = input.readInt();
                    MolangExpression[] expressions = new MolangExpression[length];
                    for (int i = 0; i < length; i++) {
                        expressions[i] = decode(input);
                    }
                    yield new MolangCompoundNode(expressions);
                }
                case LIKELY_COMPLEX_EXPRESSION_ID -> {
                    String expr = ByteBufCodecs.STRING_UTF8.decode(input);
                    MolangExpression expression;
                    try {
                        expression = PinwheelMolangCompiler.get().compile(expr);
                    } catch (MolangSyntaxException e) {
                        throw new RuntimeException(e);
                    }
                    yield expression == null ? MolangExpression.ZERO : expression;
                }
                default -> {
                    BiscuitRoll.LOGGER.warn("Received unsupported molang expression with id {}", id);
                    yield MolangExpression.ZERO;
                }
            };
        }

        @Override
        public void encode(ByteBuf output, MolangExpression value) { //other nodes that are not supported by this codec are either rely on suppliers or idk how encode them
            switch (value) {
                case MolangVariableNode expr -> {
                    output.writeByte(0);
                    output.writeFloat(expr.getValue());
                }
                case MolangConstantNode expr -> {
                    output.writeByte(1);
                    output.writeFloat(expr.getConstant());
                }
                case MolangCompoundNode expr -> {
                    output.writeByte(2);
                    output.writeInt(expr.expressions().length);
                    for (int i = 0; i < expr.expressions().length; i++) {
                        encode(output, expr.expressions()[i]);
                    }
                }
                default -> {
                    //last ditch attempt
                    output.writeByte(LIKELY_COMPLEX_EXPRESSION_ID);
                    ByteBufCodecs.STRING_UTF8.encode(output, value.toString());
                }
            }
        }
    };

    public static final StreamCodec<ByteBuf, Optional<MolangExpression>> MOLANG_EXPRESSION_OPTIONAL = ByteBufCodecs.optional(MOLANG_EXPRESSION);

    public static final StreamCodec<ByteBuf, Optional<String>> STRING_OPTIONAL = ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8);

    public static final StreamCodec<ByteBuf, Vector3fc[]> VECTOR3F_ARRAY = array(ByteBufCodecs.VECTOR3F, new Vector3fc[0]);

    public static final StreamCodec<ByteBuf, Vector2f> VECTOR2F = new StreamCodec<>() {
        @Override
        public @NonNull Vector2f decode(ByteBuf input) {
            float x = input.readFloat();
            float y = input.readFloat();
            return new Vector2f(x, y);
        }

        @Override
        public void encode(ByteBuf output, Vector2f value) {
            output.writeFloat(value.x);
            output.writeFloat(value.y);
        }
    };

    public static final StreamCodec<ByteBuf, Vector2f[]> VECTOR2F_ARRAY = array(VECTOR2F, new Vector2f[0]);

    public static final StreamCodec<FriendlyByteBuf, GeometryModelData.Polygon> POLYGON = new StreamCodec<>() {
        @Override
        public void encode(FriendlyByteBuf output, GeometryModelData.Polygon value) {
            output.writeVarIntArray(value.positions());
            output.writeVarIntArray(value.normals());
            output.writeVarIntArray(value.uvs());
        }

        @Override
        public GeometryModelData.@NonNull Polygon decode(FriendlyByteBuf input) {
            int[] pos = input.readVarIntArray(4);
            int[] norm = input.readVarIntArray(4);
            int[] uv = input.readVarIntArray(4);
            return new GeometryModelData.Polygon(pos, norm, uv);
        }
    };

    public static final StreamCodec<FriendlyByteBuf, GeometryModelData.Polygon[]> POLYGON_ARRAY = array(POLYGON, new GeometryModelData.Polygon[0]);

    public static final StreamCodec<ByteBuf, GeometryModelData.PolyType> POLY_TYPE = ByteBufCodecs.idMapper(ByIdMap.continuous(Enum::ordinal, GeometryModelData.PolyType.values(), ByIdMap.OutOfBoundsStrategy.ZERO), Enum::ordinal);

    public static final StreamCodec<FriendlyByteBuf, GeometryModelData.PolyMesh> POLY_MESH = new StreamCodec<>() {
        @Override
        public GeometryModelData.@NonNull PolyMesh decode(FriendlyByteBuf input) {
            boolean normalizedUvs = input.readBoolean();
            Vector3fc[] positionsc = VECTOR3F_ARRAY.decode(input);
            Vector3f[] positions = new Vector3f[positionsc.length];
            for (int i = 0; i < positionsc.length; i++) {
                positions[i] = new Vector3f(positionsc[i]);
            }
            Vector3fc[] normalsc = VECTOR3F_ARRAY.decode(input);
            Vector3f[] normals = new Vector3f[normalsc.length];
            for (int i = 0; i < normalsc.length; i++) {
                normals[i] = new Vector3f(normalsc[i]);
            }
            Vector2f[] uvs = VECTOR2F_ARRAY.decode(input);
            GeometryModelData.Polygon[] polys = POLYGON_ARRAY.decode(input);
            GeometryModelData.PolyType polyType = POLY_TYPE.decode(input);
            return new GeometryModelData.PolyMesh(normalizedUvs, positions, normals, uvs, polys, polyType);
        }

        @Override
        public void encode(FriendlyByteBuf output, GeometryModelData.PolyMesh value) {
            output.writeBoolean(value.normalizedUvs());
            VECTOR3F_ARRAY.encode(output, value.positions());
            VECTOR3F_ARRAY.encode(output, value.normals());
            VECTOR2F_ARRAY.encode(output, value.uvs());
            POLYGON_ARRAY.encode(output, value.polys());
            POLY_TYPE.encode(output, value.polyType());
        }
    };

    public static final StreamCodec<FriendlyByteBuf, Optional<GeometryModelData.PolyMesh>> POLY_MESH_OPTIONAL = ByteBufCodecs.optional(POLY_MESH);

    public static final StreamCodec<ByteBuf, GeometryModelData.CubeUVRotation> CUBE_UV_ROTATION = ByteBufCodecs.idMapper(ByIdMap.continuous(Enum::ordinal, GeometryModelData.CubeUVRotation.values(), ByIdMap.OutOfBoundsStrategy.ZERO), Enum::ordinal);

    public static final StreamCodec<ByteBuf, GeometryModelData.CubeUV> CUBE_UV = new StreamCodec<>() {
        @Override
        public GeometryModelData.@NonNull CubeUV decode(ByteBuf input) {
            float u = input.readFloat();
            float v = input.readFloat();
            float uSize = input.readFloat();
            float vSize = input.readFloat();
            GeometryModelData.CubeUVRotation rotation = CUBE_UV_ROTATION.decode(input);
            String materialInstance = ByteBufCodecs.STRING_UTF8.decode(input);
            return new GeometryModelData.CubeUV(u, v ,uSize, vSize, rotation, materialInstance);
        }

        @Override
        public void encode(ByteBuf output, GeometryModelData.CubeUV value) {
            output.writeFloat(value.u());
            output.writeFloat(value.v());
            output.writeFloat(value.uSize());
            output.writeFloat(value.vSize());
            CUBE_UV_ROTATION.encode(output, value.rotation());
            ByteBufCodecs.STRING_UTF8.encode(output, value.materialInstance());
        }
    };

    public static final StreamCodec<ByteBuf, GeometryModelData.CubeUV[]> CUBE_UV_ARRAY = array(CUBE_UV, new GeometryModelData.CubeUV[0]);

    public static final StreamCodec<ByteBuf, Optional<GeometryModelData.CubeUV>> CUBE_UV_OPTIONAL = ByteBufCodecs.optional(CUBE_UV);

    public static final StreamCodec<ByteBuf, Optional<GeometryModelData.CubeUV>[]> CUBE_UV_OPTIONAL_ARRAY = array(CUBE_UV_OPTIONAL, new Optional[0]);

    public static final StreamCodec<ByteBuf, GeometryModelData.Cube> CUBE = new StreamCodec<>() {
        @Override
        public GeometryModelData.@NonNull Cube decode(@NonNull ByteBuf input) {
            Vector3f origin = (Vector3f) ByteBufCodecs.VECTOR3F.decode(input);
            Vector3f size = (Vector3f) ByteBufCodecs.VECTOR3F.decode(input);
            Vector3f rotation = (Vector3f) ByteBufCodecs.VECTOR3F.decode(input);
            Vector3f pivot = (Vector3f) ByteBufCodecs.VECTOR3F.decode(input);
            boolean overrideInflate = input.readBoolean();
            float inflate = input.readFloat();
            boolean overrideMirror = input.readBoolean();
            boolean mirror = input.readBoolean();
            Optional<GeometryModelData.CubeUV>[] uv = CUBE_UV_OPTIONAL_ARRAY.decode(input);
            GeometryModelData.CubeUV[] uvs = new GeometryModelData.CubeUV[uv.length];
            for (int i = 0; i < uv.length; i++) {
                uvs[i] = uv[i].orElse(null);
            }
            return new GeometryModelData.Cube(origin, size, rotation, pivot, overrideInflate, inflate, overrideMirror, mirror, uvs);
        }

        @Override
        public void encode(@NonNull ByteBuf output, GeometryModelData.Cube value) {
            ByteBufCodecs.VECTOR3F.encode(output, value.origin());
            ByteBufCodecs.VECTOR3F.encode(output, value.size());
            ByteBufCodecs.VECTOR3F.encode(output, value.rotation());
            ByteBufCodecs.VECTOR3F.encode(output, value.pivot());
            output.writeBoolean(value.overrideInflate());
            output.writeFloat(value.inflate());
            output.writeBoolean(value.overrideMirror());
            Optional<GeometryModelData.CubeUV>[] uvs = new Optional[value.uv().length];
            for (int i = 0; i < value.uv().length; i++) {
                uvs[i] = Optional.ofNullable(value.uv()[i]);
            }
            CUBE_UV_OPTIONAL_ARRAY.encode(output, uvs);
        }
    };

    public static final StreamCodec<ByteBuf, GeometryModelData.Cube[]> CUBE_ARRAY = array(CUBE, new GeometryModelData.Cube[0]);

    public static final StreamCodec<ByteBuf, GeometryModelData.Locator> LOCATOR = new StreamCodec<>() {
        @Override
        public GeometryModelData.@NonNull Locator decode(@NonNull ByteBuf input) {
            String identifier = ByteBufCodecs.STRING_UTF8.decode(input);
            Vector3f position = (Vector3f) ByteBufCodecs.VECTOR3F.decode(input);
            return new GeometryModelData.Locator(identifier, position);
        }

        @Override
        public void encode(@NonNull ByteBuf output, GeometryModelData.Locator value) {
            ByteBufCodecs.STRING_UTF8.encode(output, value.identifier());
            ByteBufCodecs.VECTOR3F.encode(output, value.position());
        }
    };

    public static final StreamCodec<ByteBuf, GeometryModelData.Locator[]> LOCATOR_ARRAY = array(LOCATOR, new GeometryModelData.Locator[0]);

    public static final StreamCodec<FriendlyByteBuf, GeometryModelData.Bone> BONE = new StreamCodec<>() {
        @Override
        public GeometryModelData.@NonNull Bone decode(@NonNull FriendlyByteBuf input) {
            String name = ByteBufCodecs.STRING_UTF8.decode(input);
            boolean reset2588 = input.readBoolean();
            boolean neverRender2588 = input.readBoolean();
            String parent = STRING_OPTIONAL.decode(input).orElse(null);
            Vector3f pivot = (Vector3f) ByteBufCodecs.VECTOR3F.decode(input);
            Vector3f rotation = (Vector3f) ByteBufCodecs.VECTOR3F.decode(input);
            Vector3f bindPoseRotation2588 = (Vector3f) ByteBufCodecs.VECTOR3F.decode(input);
            boolean mirror = input.readBoolean();
            float inflate = input.readFloat();
            boolean debug = input.readBoolean();
            GeometryModelData.Cube[] cubes = CUBE_ARRAY.decode(input);
            GeometryModelData.Locator[] locators = LOCATOR_ARRAY.decode(input);
            MolangExpression binding = MOLANG_EXPRESSION_OPTIONAL.decode(input).orElse(null);
            GeometryModelData.PolyMesh polyMesh = POLY_MESH_OPTIONAL.decode(input).orElse(null);
            return new GeometryModelData.Bone(name, reset2588, neverRender2588, parent, pivot, rotation, bindPoseRotation2588, mirror, inflate, debug, cubes, locators, binding, polyMesh);
        }

        @Override
        public void encode(@NonNull FriendlyByteBuf output, GeometryModelData.Bone value) {
            ByteBufCodecs.STRING_UTF8.encode(output, value.name());
            output.writeBoolean(value.reset2588());
            output.writeBoolean(value.neverRender2588());
            STRING_OPTIONAL.encode(output, Optional.ofNullable(value.parent()));
            ByteBufCodecs.VECTOR3F.encode(output, value.pivot());
            ByteBufCodecs.VECTOR3F.encode(output, value.rotation());
            ByteBufCodecs.VECTOR3F.encode(output, value.bindPoseRotation2588());
            output.writeBoolean(value.mirror());
            output.writeFloat(value.inflate());
            output.writeBoolean(value.debug());
            CUBE_ARRAY.encode(output, value.cubes());
            LOCATOR_ARRAY.encode(output, value.locators());
            MOLANG_EXPRESSION_OPTIONAL.encode(output, Optional.ofNullable(value.binding()));
            POLY_MESH_OPTIONAL.encode(output, Optional.ofNullable(value.polyMesh()));
        }
    };

    public static final StreamCodec<FriendlyByteBuf, GeometryModelData.Bone[]> BONE_ARRAY = array(BONE, new GeometryModelData.Bone[0]);

    public static final StreamCodec<ByteBuf, GeometryModelData.Description> DESCRIPTION = new StreamCodec<>() {
        @Override
        public GeometryModelData.@NonNull Description decode(@NonNull ByteBuf input) {
            String identifier = ByteBufCodecs.STRING_UTF8.decode(input);
            float visibleBoundsWidth = input.readFloat();
            float visibleBoundsHeight = input.readFloat();
            Vector3f visibleBoundsOffset = (Vector3f) ByteBufCodecs.VECTOR3F.decode(input);
            int textureWidth = input.readInt();
            int textureHeight = input.readInt();
            boolean preserveModelPose2588 = input.readBoolean();
            return new GeometryModelData.Description(identifier, visibleBoundsWidth, visibleBoundsHeight, visibleBoundsOffset, textureWidth, textureHeight, preserveModelPose2588);
        }

        @Override
        public void encode(@NonNull ByteBuf output, GeometryModelData.Description value) {
            ByteBufCodecs.STRING_UTF8.encode(output, value.identifier());
            output.writeFloat(value.visibleBoundsWidth());
            output.writeFloat(value.visibleBoundsHeight());
            ByteBufCodecs.VECTOR3F.encode(output, value.visibleBoundsOffset());
            output.writeInt(value.textureWidth());
            output.writeInt(value.textureHeight());
            output.writeBoolean(value.preserveModelPose2588());
        }
    };

    public static final StreamCodec<FriendlyByteBuf, GeometryModelData> GEOMETRY_MODEL_DATA = new StreamCodec<>() {
        @Override
        public @NonNull GeometryModelData decode(@NonNull FriendlyByteBuf input) {
            GeometryModelData.Description description = DESCRIPTION.decode(input);
            String cape = STRING_OPTIONAL.decode(input).orElse(null);
            GeometryModelData.Bone[] bones = BONE_ARRAY.decode(input);
            return new GeometryModelData(description, cape, bones);
        }

        @Override
        public void encode(@NonNull FriendlyByteBuf output, GeometryModelData value) {
            DESCRIPTION.encode(output, value.description());
            STRING_OPTIONAL.encode(output, Optional.ofNullable(value.cape()));
            BONE_ARRAY.encode(output, value.bones());
        }
    };

    public static final StreamCodec<ByteBuf, AnimationData.Loop> LOOP = ByteBufCodecs.idMapper(ByIdMap.continuous(Enum::ordinal, AnimationData.Loop.values(), ByIdMap.OutOfBoundsStrategy.ZERO), Enum::ordinal);

    public static final StreamCodec<ByteBuf, AnimationData.LerpMode> LERP_MODE = ByteBufCodecs.idMapper(ByIdMap.continuous(Enum::ordinal, AnimationData.LerpMode.values(), ByIdMap.OutOfBoundsStrategy.ZERO), Enum::ordinal);

    public static final StreamCodec<ByteBuf, AnimationData.KeyFrame> KEY_FRAME = new StreamCodec<>() {
        @Override
        public AnimationData.KeyFrame decode(ByteBuf input) {
            float time = input.readFloat();
            AnimationData.LerpMode lerpMode = LERP_MODE.decode(input);
            MolangExpression transformPreX = MOLANG_EXPRESSION.decode(input);
            MolangExpression transformPreY = MOLANG_EXPRESSION.decode(input);
            MolangExpression transformPreZ = MOLANG_EXPRESSION.decode(input);
            MolangExpression transformPostX = MOLANG_EXPRESSION.decode(input);
            MolangExpression transformPostY = MOLANG_EXPRESSION.decode(input);
            MolangExpression transformPostZ = MOLANG_EXPRESSION.decode(input);
            return new AnimationData.KeyFrame(time, lerpMode, transformPreX, transformPreY, transformPreZ, transformPostX, transformPostY, transformPostZ);
        }

        @Override
        public void encode(ByteBuf output, AnimationData.KeyFrame value) {
            output.writeFloat(value.time());
            LERP_MODE.encode(output, value.lerpMode());
            MOLANG_EXPRESSION.encode(output, value.transformPreX());
            MOLANG_EXPRESSION.encode(output, value.transformPreY());
            MOLANG_EXPRESSION.encode(output, value.transformPreZ());
            MOLANG_EXPRESSION.encode(output, value.transformPostX());
            MOLANG_EXPRESSION.encode(output, value.transformPostY());
            MOLANG_EXPRESSION.encode(output, value.transformPostZ());
        }
    };

    public static final StreamCodec<ByteBuf, AnimationData.KeyFrame[]> KEY_FRAME_ARRAY = array(KEY_FRAME, new AnimationData.KeyFrame[0]);

    public static final StreamCodec<ByteBuf, AnimationData.BoneAnimation> BONE_ANIMATION = new StreamCodec<>() {
        @Override
        public AnimationData.BoneAnimation decode(ByteBuf input) {
            String name = ByteBufCodecs.STRING_UTF8.decode(input);
            AnimationData.KeyFrame[] positionFrames = KEY_FRAME_ARRAY.decode(input);
            AnimationData.KeyFrame[] rotationFrames = KEY_FRAME_ARRAY.decode(input);
            AnimationData.KeyFrame[] scaleFrames = KEY_FRAME_ARRAY.decode(input);
            return new AnimationData.BoneAnimation(name, positionFrames, rotationFrames,scaleFrames);
        }

        @Override
        public void encode(ByteBuf output, AnimationData.BoneAnimation value) {
            ByteBufCodecs.STRING_UTF8.encode(output, value.name());
            KEY_FRAME_ARRAY.encode(output, value.positionFrames());
            KEY_FRAME_ARRAY.encode(output, value.rotationFrames());
            KEY_FRAME_ARRAY.encode(output, value.scaleFrames());
        }
    };

    public static final StreamCodec<ByteBuf, AnimationData.BoneAnimation[]> BONE_ANIMATION_ARRAY = array(BONE_ANIMATION, new AnimationData.BoneAnimation[0]);

    public static final StreamCodec<ByteBuf, AnimationData.SoundEffect> SOUND_EFFECT = new StreamCodec<>() {
        @Override
        public AnimationData.SoundEffect decode(ByteBuf input) {
            float time = input.readFloat();
            String effect = ByteBufCodecs.STRING_UTF8.decode(input);
            MolangExpression pitch = MOLANG_EXPRESSION.decode(input);
            MolangExpression volume = MOLANG_EXPRESSION.decode(input);
            boolean loop = input.readBoolean();
            return new AnimationData.SoundEffect(time, effect, pitch, volume, loop);
        }

        @Override
        public void encode(ByteBuf output, AnimationData.SoundEffect value) {
            output.writeFloat(value.time());
            ByteBufCodecs.STRING_UTF8.encode(output, value.effect());
            MOLANG_EXPRESSION.encode(output, value.pitch());
            MOLANG_EXPRESSION.encode(output, value.volume());
            output.writeBoolean(value.loop());
        }
    };

    public static final StreamCodec<ByteBuf, AnimationData.SoundEffect[]> SOUND_EFFECT_ARRAY = array(SOUND_EFFECT, new AnimationData.SoundEffect[0]);

    public static final StreamCodec<ByteBuf, AnimationData.ParticleEffect> PARTICLE_EFFECT = new StreamCodec<>() {
        @Override
        public AnimationData.ParticleEffect decode(ByteBuf input) {
            float time = input.readFloat();
            String effect = ByteBufCodecs.STRING_UTF8.decode(input);
            String locator = ByteBufCodecs.STRING_UTF8.decode(input);
            return new AnimationData.ParticleEffect(time, effect, locator);
        }

        @Override
        public void encode(ByteBuf output, AnimationData.ParticleEffect value) {
            output.writeFloat(value.time());
            ByteBufCodecs.STRING_UTF8.encode(output, value.effect());
            ByteBufCodecs.STRING_UTF8.encode(output, value.locator());
        }
    };

    public static final StreamCodec<ByteBuf, AnimationData.ParticleEffect[]> PARTICLE_EFFECT_ARRAY = array(PARTICLE_EFFECT, new AnimationData.ParticleEffect[0]);

    public static final StreamCodec<ByteBuf, AnimationData.TimelineEffect> TIMELINE_EFFECT = new StreamCodec<>() {
        @Override
        public AnimationData.TimelineEffect decode(ByteBuf input) {
            float time = input.readFloat();
            String data = ByteBufCodecs.STRING_UTF8.decode(input);
            return new AnimationData.TimelineEffect(time, data);
        }

        @Override
        public void encode(ByteBuf output, AnimationData.TimelineEffect value) {
            output.writeFloat(value.time());
            ByteBufCodecs.STRING_UTF8.encode(output, value.data());
        }
    };

    public static final StreamCodec<ByteBuf, AnimationData.TimelineEffect[]> TIMELINE_EFFECT_ARRAY = array(TIMELINE_EFFECT, new AnimationData.TimelineEffect[0]);

    public static final StreamCodec<ByteBuf, AnimationData> ANIMATION_DATA = new StreamCodec<>() {
        @Override
        public AnimationData decode(ByteBuf input) {
            String name = ByteBufCodecs.STRING_UTF8.decode(input);
            AnimationData.Loop loop = LOOP.decode(input);
            MolangExpression blendWeight = MOLANG_EXPRESSION.decode(input);
            float animationLength = input.readFloat();
            boolean overridePreviousAnimation = input.readBoolean();
            AnimationData.BoneAnimation[] boneAnimations = BONE_ANIMATION_ARRAY.decode(input);
            AnimationData.SoundEffect[] soundEffects = SOUND_EFFECT_ARRAY.decode(input);
            AnimationData.ParticleEffect[] particleEffects = PARTICLE_EFFECT_ARRAY.decode(input);
            AnimationData.TimelineEffect[] timelineEffects = TIMELINE_EFFECT_ARRAY.decode(input);
            return new AnimationData(name, loop, blendWeight, animationLength, overridePreviousAnimation, boneAnimations, soundEffects, particleEffects, timelineEffects);
        }

        @Override
        public void encode(ByteBuf output, AnimationData value) {
            ByteBufCodecs.STRING_UTF8.encode(output, value.name());
            LOOP.encode(output, value.loop());
            MOLANG_EXPRESSION.encode(output, value.blendWeight());
            output.writeFloat(value.animationLength());
            output.writeBoolean(value.overridePreviousAnimation());
            BONE_ANIMATION_ARRAY.encode(output, value.boneAnimations());
            SOUND_EFFECT_ARRAY.encode(output, value.soundEffects());
            PARTICLE_EFFECT_ARRAY.encode(output, value.particleEffects());
            TIMELINE_EFFECT_ARRAY.encode(output, value.timelineEffects());
        }
    };

    public static final StreamCodec<ByteBuf, AnimationData[]> ANIMATION_DATA_ARRAY = array(ANIMATION_DATA, new AnimationData[0]);

    public static <B extends ByteBuf, T> StreamCodec<B, T[]> array(final @NonNull StreamCodec<B, T> streamCodec, T[] dummyArray) {
        return new StreamCodec<>() {
            @Override
            public T @NonNull [] decode(@NonNull B input) {
                int length = input.readInt();
                T[] keyFrames = Arrays.copyOf(dummyArray, length);
                for (int i = 0; i < keyFrames.length; i++) {
                    keyFrames[i] = streamCodec.decode(input);
                }
                return keyFrames;
            }

            @Override
            public void encode(@NonNull B output, T @NonNull [] value) {
                output.writeInt(value.length);
                for (T keyFrame : value) {
                    streamCodec.encode(output, keyFrame);
                }
            }
        };
    }
}
