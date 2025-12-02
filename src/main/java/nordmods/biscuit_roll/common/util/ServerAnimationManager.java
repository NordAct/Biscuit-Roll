package nordmods.biscuit_roll.common.util;

import gg.moonflower.pinwheel.api.animation.AnimationData;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public class ServerAnimationManager extends BRAnimationManager {
    private static final ServerAnimationManager INSTANCE = new ServerAnimationManager();
    private static final Map<Identifier, AnimationData[]> ANIMATION_REGISTRY = new HashMap<>();

    private ServerAnimationManager() {

    }

    @Override
    protected Map<Identifier, AnimationData[]> getHolderMap() {
        return ANIMATION_REGISTRY;
    }

    public static ServerAnimationManager instance() {
        return INSTANCE;
    }
}
