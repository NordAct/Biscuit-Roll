package nordmods.biscuit_roll.common.util;

import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.common.model.BRModel;

import java.util.HashMap;
import java.util.Map;

public class ServerModelManager extends BRModelManager {
    private static final ServerModelManager INSTANCE = new ServerModelManager();
    private final Map<Identifier, BRModel> modelRegistry = new HashMap<>();

    private ServerModelManager() {

    }

    @Override
    protected Map<Identifier, BRModel> getHolderMap() {
        return modelRegistry;
    }

    public static ServerModelManager instance() {
        return INSTANCE;
    }
}
