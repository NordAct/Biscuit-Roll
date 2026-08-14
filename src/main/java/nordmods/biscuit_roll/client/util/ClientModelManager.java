package nordmods.biscuit_roll.client.util;

import gg.moonflower.pinwheel.api.geometry.GeometryModelData;
import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.common.model.BRModel;
import nordmods.biscuit_roll.common.util.BRModelManager;
import nordmods.biscuit_roll.common.util.ServerModelManager;
import org.jspecify.annotations.Nullable;

public class ClientModelManager extends BRModelManager {
    private static final ClientModelManager INSTANCE = new ClientModelManager();

    private ClientModelManager() {}

    public static ClientModelManager instance() {
        return INSTANCE;
    }

    @Override
    public @Nullable GeometryModelData getModelData(Identifier modelId) {
        GeometryModelData geometryModelData = ServerModelManager.instance().getModelData(modelId);
        if (geometryModelData != null) return geometryModelData;
        return super.getModelData(modelId);
    }

    @Override
    public @Nullable BRModel getModel(Identifier modelId) {
        BRModel model = ServerModelManager.instance().getModel(modelId);
        if (model != null) return model;
        return super.getModel(modelId);
    }
}
