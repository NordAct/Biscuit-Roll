package nordmods.biscuit_roll.client.resource_managers;

import gg.moonflower.pinwheel.api.geometry.GeometryModelData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import nordmods.biscuit_roll.common.model.PolyMeshAttachments;
import nordmods.biscuit_roll.common.resource_managers.BRModelManager;
import nordmods.biscuit_roll.common.resource_managers.BRPolyMeshAttachmentsManager;
import nordmods.biscuit_roll.common.resource_managers.ServerModelManager;
import nordmods.biscuit_roll.common.resource_managers.ServerPolyMeshAttachmentsManager;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ClientModelManager extends BRModelManager {
    private static final ClientModelManager INSTANCE = new ClientModelManager();
    private final Map<Identifier, GeometryModelData> modelDataCache = new HashMap<>();

    private ClientModelManager() {}

    public static ClientModelManager instance() {
        return INSTANCE;
    }

    @Override
    public @Nullable GeometryModelData getModelData(Identifier modelId) {
        return modelDataCache.computeIfAbsent(modelId, id -> {
            GeometryModelData geometryModelData = ServerModelManager.instance().getModelData(id);
            if (geometryModelData != null) return geometryModelData;
            return super.getModelData(id);
        });
    }

    @Override
    public @Nullable PolyMeshAttachments getPolymeshAttachments(Identifier modelId) {
        Identifier id = BRPolyMeshAttachmentsManager.CONVERTER.idToFile(CONVERTER.fileToId(modelId));
        PolyMeshAttachments attachments = ServerPolyMeshAttachmentsManager.instance().getAttachments(id);
        if (attachments != null) return attachments;
        return ClientPolyMeshAttachmentsManager.instance().getAttachments(id);

    }

    @Override
    protected void apply(Map<Identifier, GeometryModelData> map, @NonNull ResourceManager resourceManager, @NonNull ProfilerFiller profilerFiller) {
        super.apply(map, resourceManager, profilerFiller);
        clearCache();
    }

    public void clearCache() {
        modelDataCache.clear();
        getRegistry().clear();
    }

    @Override
    public boolean hasModel(Identifier modelId) {
        return super.hasModel(modelId) || ServerModelManager.instance().hasModel(modelId);
    }
}
