package nordmods.biscuit_roll.client.resource_managers;

import gg.moonflower.pinwheel.api.animation.AnimationData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import nordmods.biscuit_roll.common.resource_managers.BRAnimationManager;
import nordmods.biscuit_roll.common.resource_managers.ServerAnimationManager;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ClientAnimationManager extends BRAnimationManager {
    private static final ClientAnimationManager INSTANCE = new ClientAnimationManager();
    private final Map<Identifier, AnimationData[]> animationCache = new HashMap<>();

    private ClientAnimationManager() {}

    public static ClientAnimationManager instance() {
        return INSTANCE;
    }

    @Override
    public @Nullable AnimationData[] getAnimations(Identifier animationId) {
        return animationCache.computeIfAbsent(animationId, id -> {
            AnimationData[] animationData = ServerAnimationManager.instance().getAnimations(id);
            if (animationData != null) return animationData;
            return super.getAnimations(id);
        });
    }

    @Override
    protected void apply(Map<Identifier, AnimationData[]> map, @NonNull ResourceManager resourceManager, @NonNull ProfilerFiller profilerFiller) {
        super.apply(map, resourceManager, profilerFiller);
        clearCache();
    }

    public void clearCache() {
        animationCache.clear();
    }

    @Override
    public boolean hasAnimations(Identifier animationId) {
        return super.hasAnimations(animationId) || ServerAnimationManager.instance().hasAnimations(animationId);
    }
}
