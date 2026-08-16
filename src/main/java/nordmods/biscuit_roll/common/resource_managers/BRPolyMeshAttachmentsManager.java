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

    public abstract Map<Identifier, PolyMeshAttachments> getRegistry();

    @Nullable
    public PolyMeshAttachments getAttachments(Identifier id) {
        return getRegistry().get(id);
    }
}
