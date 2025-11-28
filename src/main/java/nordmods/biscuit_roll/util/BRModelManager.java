package nordmods.biscuit_roll.util;

import gg.moonflower.pinwheel.api.geometry.GeometryModel;
import gg.moonflower.pinwheel.api.geometry.GeometryModelData;
import gg.moonflower.pinwheel.api.geometry.GeometryModelParser;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import nordmods.biscuit_roll.BiscuitRoll;
import nordmods.biscuit_roll.model.BRModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public abstract class BRModelManager extends SimplePreparableReloadListener<Map<Identifier, GeometryModel>>{
    private static final String FOLDER = BiscuitRoll.MOD_ID + "/models";

    @Override
    protected Map<Identifier, GeometryModel> prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        Map<Identifier, GeometryModel> holder = new HashMap<>();

        Map<Identifier, Resource> resourceMap = resourceManager.listResources(FOLDER, path -> path.getPath().endsWith(".json"));

        for(Map.Entry<Identifier, Resource> entry: resourceMap.entrySet()) {
            Identifier fileId = entry.getKey();

            try (InputStream stream = resourceManager.getResource(fileId).get().open()) {
                InputStreamReader inputStreamReader = new InputStreamReader(stream, StandardCharsets.UTF_8);
                GeometryModelData[] data = GeometryModelParser.parseModel(inputStreamReader);
                String path = fileId.getPath();
                String modelPath = path.substring(FOLDER.length() + 1, path.indexOf(".json"));
                Identifier modelId = fileId.withPath(modelPath);
                if (data.length == 1) {
                    holder.put(modelId, new BRModel(data[0]));
                    BiscuitRoll.LOGGER.info("Registered model with id {}", modelId);
                } else {
                    BiscuitRoll.LOGGER.warn("Model file {} got more or less than 1 model and will be skipped", fileId);
                }
            } catch(Exception e) {
                BiscuitRoll.LOGGER.error("Error occurred while loading resource json {}", fileId, e);
            }
        }
        return holder;
    }

    @Override
    protected void apply(Map<Identifier, GeometryModel> map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        getHolderMap().clear();
        getHolderMap().putAll(map);
    }

    @Nullable
    public GeometryModel getModel(@NotNull Identifier modelId) {
        return getHolderMap().get(modelId);
    }

    protected abstract Map<Identifier, GeometryModel> getHolderMap();
}
