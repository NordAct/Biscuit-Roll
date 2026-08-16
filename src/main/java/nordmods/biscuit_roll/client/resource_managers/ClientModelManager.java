package nordmods.biscuit_roll.client.resource_managers;

import gg.moonflower.pinwheel.api.geometry.GeometryModelData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import nordmods.biscuit_roll.common.model.BRModel;
import nordmods.biscuit_roll.common.resource_managers.BRModelManager;
import nordmods.biscuit_roll.common.resource_managers.ServerModelManager;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ClientModelManager extends BRModelManager {
    private static final ClientModelManager INSTANCE = new ClientModelManager();
    private final Map<Identifier, BRModel> modelCache = new HashMap<>();
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
    public @Nullable BRModel getModel(Identifier modelId) {
        return modelCache.computeIfAbsent(modelId, id -> {
            BRModel model = ServerModelManager.instance().getModel(id);
            if (model != null) return model;
            return super.getModel(id);
        });
    }

    @Override
    protected void apply(Map<Identifier, GeometryModelData> map, @NonNull ResourceManager resourceManager, @NonNull ProfilerFiller profilerFiller) {
        super.apply(map, resourceManager, profilerFiller);
        clearCache();
    }

    public void clearCache() {
        modelDataCache.clear();
        modelCache.clear();
    }
}
