package nordmods.biscuit_roll.common.model;

import com.mojang.serialization.Codec;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector3fc;

import java.util.Map;

public record PolyMeshAttachments(Map<String, Map<String, Vector3fc>> attachments) {
    public static final Codec<PolyMeshAttachments> CODEC = Codec.unboundedMap(
            Codec.STRING,
            Codec.unboundedMap(Codec.STRING, ExtraCodecs.VECTOR3F)
    ).xmap(PolyMeshAttachments::new, PolyMeshAttachments::attachments);
}
