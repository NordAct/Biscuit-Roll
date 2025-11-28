package nordmods.biscuit_roll.client.util;

import gg.moonflower.pinwheel.api.geometry.GeometryModel;
import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.util.BRModelManager;

import java.util.HashMap;
import java.util.Map;

public class ClientModelManager extends BRModelManager {
    private static final ClientModelManager INSTANCE = new ClientModelManager();
    private static final Map<Identifier, GeometryModel> MODEL_REGISTRY = new HashMap<>();

    private ClientModelManager() {

    }

    @Override
    protected Map<Identifier, GeometryModel> getHolderMap() {
        return MODEL_REGISTRY;
    }

    public static ClientModelManager instance() {
        return INSTANCE;
    }
}
