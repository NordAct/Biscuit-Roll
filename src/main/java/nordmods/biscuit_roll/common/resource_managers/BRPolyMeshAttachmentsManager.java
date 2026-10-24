package nordmods.biscuit_roll.common.resource_managers;

import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import nordmods.biscuit_roll.BiscuitRoll;
import nordmods.biscuit_roll.common.model.PolyMeshAttachments;
import org.jspecify.annotations.Nullable;

import java.util.Map;

/// Loads polygonal mesh attachments for model.\
/// Json is supposed to look something like that
/// ```json
/// {
///     "bone1": {
///         "locator1": [x1, y1, z1],
///         "locator2": [x2, y2, z2]
///     },
///     "bone2": {
///         "locator3": [x3, y3, z3],
///         "locator4": [x4, y4, z4]
///     }
/// }
/// ```
/// Where:
/// - `boneN` - name of the bone, to which poly mesh belongs
/// - `locatorN` - name of locator on model to which specified on right vertices will be attached to
/// - `[xN, yN, zN]` - vertices position vector how they are in exported file\
/// Bone, to which poly mesh is attached to, must not be animated or affected by animations.
/// Otherwise it may render incorrectly, since poly mesh attachments do not account for parent bone transformations
public abstract class BRPolyMeshAttachmentsManager extends SimpleJsonResourceReloadListener<PolyMeshAttachments> {
    public final static FileToIdConverter CONVERTER = FileToIdConverter.json(BiscuitRoll.MOD_ID + "/poly_mesh_attachments");
    protected BRPolyMeshAttachmentsManager() {
        super(PolyMeshAttachments.CODEC, CONVERTER);
    }

    @Override
    protected void apply(Map<Identifier, PolyMeshAttachments> preparations, ResourceManager manager, ProfilerFiller profiler) {
        getRegistry().clear();
        preparations.forEach((id, attchment) -> getRegistry().put(CONVERTER.idToFile(id), attchment));
    }

    /// @return loaded poly mesh attachments by file id
    public abstract Map<Identifier, PolyMeshAttachments> getRegistry();

    /// @param id id of model file
    /// @return attachments for specified model
    @Nullable
    public PolyMeshAttachments getAttachments(Identifier id) {
        return getRegistry().get(id);
    }

    /// @param id id of model file
    /// @return if attachments for specified model exist
    public boolean hasAttachments(Identifier id) {
        return getRegistry().containsKey(id);
    }
}
