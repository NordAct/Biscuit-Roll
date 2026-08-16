package nordmods.biscuit_roll.common.resource_managers;

import gg.moonflower.pinwheel.api.animation.AnimationData;
import gg.moonflower.pinwheel.api.animation.AnimationParser;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import nordmods.biscuit_roll.BiscuitRoll;
import nordmods.biscuit_roll.client.resource_managers.ClientAnimationManager;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/// Responsible for loading animations. Use [BRAnimationManager#getAnimationManager(boolean isClient)] to get animation manager for correct side
@ApiStatus.NonExtendable
public abstract class BRAnimationManager extends SimplePreparableReloadListener<Map<Identifier, AnimationData[]>>{
    private static final String FOLDER = BiscuitRoll.MOD_ID + "/animations";
    private final Map<Identifier, AnimationData[]> animationRegistry = new HashMap<>();

    @Override
    protected Map<Identifier, AnimationData[]> prepare(ResourceManager resourceManager, @NonNull ProfilerFiller profilerFiller) {
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
    protected void apply(Map<Identifier, AnimationData[]> map, @NonNull ResourceManager resourceManager, @NonNull ProfilerFiller profilerFiller) {
        getRegistry().clear();
        getRegistry().putAll(map);
    }

    /// @param animationId id of animation file
    /// @return all animations from the file as [AnimationData]
    @Nullable
    public AnimationData[] getAnimations(Identifier animationId) {
        return getRegistry().get(animationId);
    }

    /// @param animationId id of animation file
    /// @param animationName name of animation to get
    /// @return [AnimationData] for specified animation
    /// @throws NoSuchElementException if specified animation doesn't exist in specified animation file or if specified animation file is not loaded
    public AnimationData getAnimation(Identifier animationId, String animationName) {
        AnimationData[] data = getAnimations(animationId);
        if (data == null) throw new NoSuchElementException("Animation file " + "'" + animationId + "'" + " is not loaded. Check log for errors and ensure that specified animation file location is correct");
        return Arrays
                .stream(data)
                .filter(animationData -> animationData.name().equals(animationName))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Couldn't find animation " + "'" + animationName + "'" + " in " + "'" + animationId + "'. Ensure that specified animation name is correct"));
    }

    public Map<Identifier, AnimationData[]> getRegistry() {
        return animationRegistry;
    }

    /// @param isClient is client side
    /// @return animation manager for specified side
    public static BRAnimationManager getAnimationManager(boolean isClient) {
        return isClient ? ClientAnimationManager.instance() : ServerAnimationManager.instance();
    }
}
