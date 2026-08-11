package nordmods.biscuit_roll.client.util;

import nordmods.biscuit_roll.common.util.BRAnimationManager;

public class ClientAnimationManager extends BRAnimationManager {
    private static final ClientAnimationManager INSTANCE = new ClientAnimationManager();

    private ClientAnimationManager() {

    }

    public static ClientAnimationManager instance() {
        return INSTANCE;
    }
}
