package nordmods.biscuit_roll.common.model;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import gg.moonflower.pinwheel.api.geometry.GeometryCompileException;
import gg.moonflower.pinwheel.api.geometry.GeometryModelData;
import gg.moonflower.pinwheel.api.geometry.GeometryTree;
import gg.moonflower.pinwheel.api.geometry.bone.AnimatedBone;
import gg.moonflower.pinwheel.api.geometry.bone.Polygon;
import gg.moonflower.pinwheel.api.geometry.bone.Vertex;
import gg.moonflower.pinwheel.api.transform.LocatorTransformation;
import net.minecraft.util.Util;
import nordmods.biscuit_roll.common.resource_managers.BRModelManager;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.jspecify.annotations.Nullable;

import java.util.*;

/// Model to be animated and rendered
/// @see BRModelManager
public class RenderableModel extends BRModel {
    private final Map<String, List<Pair<String, Vector3f>>> polymeshPositions;

    public RenderableModel(GeometryModelData model, @Nullable PolyMeshAttachments polyMeshAttachments) throws GeometryCompileException {
        super(model);
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
    public GeometryTree createGeometryTree(GeometryModelData model) throws GeometryCompileException{
        return GeometryTree.create(model);
    }

    @Override
    public void updateLocators() {
        super.updateLocators();
        polymeshPositions.forEach((_, attachments) -> attachments.forEach(pair -> {
            LocatorTransformation transformation = getLocatorTransformation(pair.getFirst());
            if (transformation != null) {
                Vector4f pos = transformation.matrix().transform(new Vector4f(0, 0, 0, 1));
                pair.getSecond().set(-pos.x(), -pos.y(), pos.z());
            }
        }));
    }
}
