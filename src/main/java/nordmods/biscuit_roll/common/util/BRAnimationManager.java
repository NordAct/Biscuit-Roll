package nordmods.biscuit_roll.common.util;

import gg.moonflower.pinwheel.api.animation.AnimationData;
import gg.moonflower.pinwheel.api.animation.AnimationParser;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import nordmods.biscuit_roll.BiscuitRoll;
import nordmods.biscuit_roll.client.util.ClientAnimationManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@ApiStatus.Internal
public abstract class BRAnimationManager extends SimplePreparableReloadListener<Map<Identifier, AnimationData[]>>{
    private static final String FOLDER = BiscuitRoll.MOD_ID + "/animations";

    @Override
    protected Map<Identifier, AnimationData[]> prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        Map<Identifier, AnimationData[]> holder = new HashMap<>();

        Map<Identifier, Resource> resourceMap = resourceManager.listResources(FOLDER, path -> path.getPath().endsWith(".json"));

        for(Map.Entry<Identifier, Resource> entry: resourceMap.entrySet()) {
            Identifier fileId = entry.getKey();

            try (InputStream stream = resourceManager.getResource(fileId).orElseThrow().open()) {
                InputStreamReader inputStreamReader = new InputStreamReader(stream, StandardCharsets.UTF_8);
                AnimationData[] data = AnimationParser.parse(inputStreamReader);
                holder.put(fileId, data);
                BiscuitRoll.LOGGER.info("Registered animation file with id {}", fileId);
            } catch(Exception e) {
                BiscuitRoll.LOGGER.error("Error occurred while loading resource json {}", fileId, e);
            }
        }
        return holder;
    }

    @Override
    protected void apply(Map<Identifier, AnimationData[]> map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        getHolderMap().clear();
        getHolderMap().putAll(map);
    }

    @Nullable
    public AnimationData[] getAnimations(Identifier animationId) {
        return getHolderMap().get(animationId);
    }

    @Nullable
    public AnimationData getAnimation(Identifier animationId, String animationName) {
        AnimationData[] data = getAnimations(animationId);
        return Arrays
                .stream(data)
                .filter(animationData -> animationData.name().equals(animationName))
                .findFirst()
                .orElseThrow();
    }

    protected abstract Map<Identifier, AnimationData[]> getHolderMap();

    public static BRAnimationManager getAnimationManager(boolean isClient) {
        return isClient ? ClientAnimationManager.instance() : ServerAnimationManager.instance();
    }
}
