package nordmods.biscuit_roll.common.resource_managers;

import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.common.model.PolyMeshAttachments;
import org.jspecify.annotations.Nullable;

public class ServerModelManager extends BRModelManager {
    private static final ServerModelManager INSTANCE = new ServerModelManager();

    private ServerModelManager() {}

    public static ServerModelManager instance() {
        return INSTANCE;
    }

    @Override
    public @Nullable PolyMeshAttachments getPolymeshAttachments(Identifier modelId) {
        return null;
    }
}
