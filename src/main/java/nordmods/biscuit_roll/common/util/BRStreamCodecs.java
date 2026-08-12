package nordmods.biscuit_roll.common.util;

import gg.moonflower.molangcompiler.api.MolangExpression;
import gg.moonflower.pinwheel.api.geometry.GeometryModelData;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public class BRStreamCodecs {
    public static final StreamCodec<ByteBuf, Optional<String>> STRING_OPTIONAL = ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8);

    public static final StreamCodec<ByteBuf, Vector3f[]> VECTOR3F_ARRAY = new StreamCodec<>() {
        @Override
        public Vector3f @NonNull [] decode(ByteBuf input) {
            Vector3f[] vectors = new Vector3f[input.readInt()];
            for (int i = 0; i < vectors.length; i++) {
                vectors[i] = (Vector3f) ByteBufCodecs.VECTOR3F.decode(input);
            }
            return vectors;
        }

        @Override
        public void encode(ByteBuf output, Vector3f[] value) {
            output.writeInt(value.length);
            for (Vector3f vector3f : value) {
                ByteBufCodecs.VECTOR3F.encode(output, vector3f);
            }
        }
    };

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

    public static final StreamCodec<ByteBuf, Vector2f[]> VECTOR2F_ARRAY = new StreamCodec<>() {
        @Override
        public Vector2f @NonNull [] decode(ByteBuf input) {
            Vector2f[] vectors = new Vector2f[input.readInt()];
            for (int i = 0; i < vectors.length; i++) {
                vectors[i] = VECTOR2F.decode(input);
            }
            return vectors;
        }

        @Override
        public void encode(ByteBuf output, Vector2f[] value) {
            output.writeInt(value.length);
            for (Vector2f vector2f : value) {
                VECTOR2F.encode(output, vector2f);
            }
        }
    };

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

    public static final StreamCodec<FriendlyByteBuf, GeometryModelData.Polygon[]> POLYGON_ARRAY = new StreamCodec<>() {
        @Override
        public GeometryModelData.Polygon @NonNull [] decode(FriendlyByteBuf input) {
            GeometryModelData.Polygon[] polygons = new GeometryModelData.Polygon[input.readInt()];
            for (int i = 0; i < polygons.length; i++) {
                polygons[i] = POLYGON.decode(input);
            }
            return polygons;
        }

        @Override
        public void encode(FriendlyByteBuf output, GeometryModelData.Polygon[] value) {
            output.writeInt(value.length);
            for (GeometryModelData.Polygon polygon : value) {
                POLYGON.encode(output, polygon);
            }
        }
    };

    public static final StreamCodec<ByteBuf, GeometryModelData.PolyType> POLY_TYPE = ByteBufCodecs.idMapper(ByIdMap.continuous(Enum::ordinal, GeometryModelData.PolyType.values(), ByIdMap.OutOfBoundsStrategy.ZERO), Enum::ordinal);

    public static final StreamCodec<FriendlyByteBuf, GeometryModelData.PolyMesh> POLY_MESH = new StreamCodec<>() {
        @Override
        public GeometryModelData.@NonNull PolyMesh decode(FriendlyByteBuf input) {
            boolean normalizedUvs = input.readBoolean();
            Vector3f[] positions = VECTOR3F_ARRAY.decode(input);
            Vector3f[] normals = VECTOR3F_ARRAY.decode(input);
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

    public static final StreamCodec<ByteBuf, GeometryModelData.CubeUV[]> CUBE_UV_ARRAY = new StreamCodec<>() {
        @Override
        public GeometryModelData.CubeUV @NonNull [] decode(ByteBuf input) {
            GeometryModelData.CubeUV[] cubeUVS = new GeometryModelData.CubeUV[input.readInt()];
            for (int i = 0; i < cubeUVS.length; i++) {
                cubeUVS[i] = CUBE_UV.decode(input);
            }
            return cubeUVS;
        }

        @Override
        public void encode(ByteBuf output, GeometryModelData.CubeUV[] value) {
            output.writeInt(value.length);
            for (GeometryModelData.CubeUV cubeUV : value) {
                CUBE_UV.encode(output, cubeUV);
            }
        }
    };

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
            GeometryModelData.CubeUV[] uv = CUBE_UV_ARRAY.decode(input);
            return new GeometryModelData.Cube(origin, size, rotation, pivot, overrideInflate, inflate, overrideMirror, mirror, uv);
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
            CUBE_UV_ARRAY.encode(output, value.uv());
        }
    };

    public static final StreamCodec<ByteBuf, GeometryModelData.Cube[]> CUBE_ARRAY = new StreamCodec<>() {
        @Override
        public GeometryModelData.Cube @NonNull [] decode(ByteBuf input) {
            GeometryModelData.Cube[] cubes = new GeometryModelData.Cube[input.readInt()];
            for (int i = 0; i < cubes.length; i++) {
                cubes[i] = CUBE.decode(input);
            }
            return cubes;
        }

        @Override
        public void encode(ByteBuf output, GeometryModelData.Cube[] value) {
            output.writeInt(value.length);
            for (GeometryModelData.Cube cube : value) {
                CUBE.encode(output, cube);
            }
        }
    };

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

    public static final StreamCodec<ByteBuf, GeometryModelData.Locator[]> LOCATOR_ARRAY = new StreamCodec<>() {
        @Override
        public GeometryModelData.Locator @NonNull [] decode(ByteBuf input) {
            GeometryModelData.Locator[] locators = new GeometryModelData.Locator[input.readInt()];
            for (int i = 0; i < locators.length; i++) {
                locators[i] = LOCATOR.decode(input);
            }
            return locators;
        }

        @Override
        public void encode(ByteBuf output, GeometryModelData.Locator[] value) {
            output.writeInt(value.length);
            for (GeometryModelData.Locator locator : value) {
                LOCATOR.encode(output, locator);
            }
        }
    };

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
            MolangExpression binding = null;
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
            //todo binding
            POLY_MESH_OPTIONAL.encode(output, Optional.ofNullable(value.polyMesh()));
        }
    };

    public static final StreamCodec<FriendlyByteBuf, GeometryModelData.Bone[]> BONE_ARRAY = new StreamCodec<>() {
        @Override
        public GeometryModelData.Bone @NonNull [] decode(FriendlyByteBuf input) {
            GeometryModelData.Bone[] bones = new GeometryModelData.Bone[input.readInt()];
            for (int i = 0; i < bones.length; i++) {
                bones[i] = BONE.decode(input);
            }
            return bones;
        }

        @Override
        public void encode(FriendlyByteBuf output, GeometryModelData.Bone[] value) {
            output.writeInt(value.length);
            for (GeometryModelData.Bone bone : value) {
                BONE.encode(output, bone);
            }
        }
    };

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
}
