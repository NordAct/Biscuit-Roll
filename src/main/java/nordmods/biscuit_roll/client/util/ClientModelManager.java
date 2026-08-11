package nordmods.biscuit_roll.client.util;

import nordmods.biscuit_roll.common.util.BRModelManager;

public class ClientModelManager extends BRModelManager {
    private static final ClientModelManager INSTANCE = new ClientModelManager();

    private ClientModelManager() {}

    public static ClientModelManager instance() {
        return INSTANCE;
    }
}
