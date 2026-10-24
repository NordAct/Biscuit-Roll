package nordmods.biscuit_roll.common.model;

import com.mojang.serialization.Codec;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector3fc;

import java.util.Map;

/// Used to create deformable mesh and "attach" it to specified locators.
/// It stores attachments per bone, connecting vertices to specified locators by their name, using vertices position vector on model.\
/// Listen, I know it sounds stupid, but it's only good way to differentiate between vertices on polygonal mesh with Bedrock's model format.
///
/// @see nordmods.biscuit_roll.common.resource_managers.BRPolyMeshAttachmentsManager
/// @param attachments map, containing information on attachments to locators for polygonal meshes per bone
public record PolyMeshAttachments(Map<String, Map<String, Vector3fc>> attachments) {
    public static final Codec<PolyMeshAttachments> CODEC = Codec.unboundedMap(
            Codec.STRING,
            Codec.unboundedMap(Codec.STRING, ExtraCodecs.VECTOR3F)
    ).xmap(PolyMeshAttachments::new, PolyMeshAttachments::attachments);
}
