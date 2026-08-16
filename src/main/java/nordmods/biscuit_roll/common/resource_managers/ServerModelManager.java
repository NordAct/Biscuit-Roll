package nordmods.biscuit_roll.common.resource_managers;

public class ServerModelManager extends BRModelManager {
    private static final ServerModelManager INSTANCE = new ServerModelManager();

    private ServerModelManager() {}

    public static ServerModelManager instance() {
        return INSTANCE;
    }
}
