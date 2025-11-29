package nordmods.biscuit_roll.util;

import gg.moonflower.pinwheel.api.animation.AnimationData;
import gg.moonflower.pinwheel.api.animation.AnimationParser;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import nordmods.biscuit_roll.BiscuitRoll;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@ApiStatus.Internal
public abstract class BRAnimationManager extends SimplePreparableReloadListener<@NotNull Map<Identifier, AnimationData[]>>{
    private static final String FOLDER = BiscuitRoll.MOD_ID + "/animations";

    @Override
    protected Map<Identifier, AnimationData[]> prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        Map<Identifier, AnimationData[]> holder = new HashMap<>();

        Map<Identifier, Resource> resourceMap = resourceManager.listResources(FOLDER, path -> path.getPath().endsWith(".json"));

        for(Map.Entry<Identifier, Resource> entry: resourceMap.entrySet()) {
            Identifier fileId = entry.getKey();

            try (InputStream stream = resourceManager.getResource(fileId).get().open()) {
                InputStreamReader inputStreamReader = new InputStreamReader(stream, StandardCharsets.UTF_8);
                AnimationData[] data = AnimationParser.parse(inputStreamReader);
                String path = fileId.getPath();
                Identifier animationId = fileId.withPath(path.substring(FOLDER.length() + 1, path.indexOf(".json")));
                holder.put(animationId, data);
                BiscuitRoll.LOGGER.info("Registered animation file with id {}", animationId);
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
    public AnimationData[] getAnimations(@NotNull Identifier animationId) {
        return getHolderMap().get(animationId);
    }

    @Nullable
    public AnimationData getAnimation(@NotNull Identifier animationId, @NotNull String animationName) {
        AnimationData[] data = getAnimations(animationId);
        return Arrays
                .stream(data)
                .filter(animationData -> animationData.name().equals(animationName))
                .findFirst()
                .orElseThrow();
    }

    protected abstract Map<Identifier, AnimationData[]> getHolderMap();
}
