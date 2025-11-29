package nordmods.biscuit_roll.client.util;

import gg.moonflower.pinwheel.api.animation.AnimationData;
import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.common.util.BRAnimationManager;

import java.util.HashMap;
import java.util.Map;

public class ClientAnimationManager extends BRAnimationManager {
    private static final ClientAnimationManager INSTANCE = new ClientAnimationManager();
    private static final Map<Identifier, AnimationData[]> ANIMATION_REGISTRY = new HashMap<>();

    private ClientAnimationManager() {

    }

    @Override
    protected Map<Identifier, AnimationData[]> getHolderMap() {
        return ANIMATION_REGISTRY;
    }

    public static ClientAnimationManager instance() {
        return INSTANCE;
    }
}
