package nordmods.biscuit_roll.common.resource_managers;

public class ServerAnimationManager extends BRAnimationManager {
    private static final ServerAnimationManager INSTANCE = new ServerAnimationManager();

    private ServerAnimationManager() {}

    public static ServerAnimationManager instance() {
        return INSTANCE;
    }
}
