package nordmods.biscuit_roll.common.resource_managers;

import gg.moonflower.pinwheel.api.geometry.GeometryCompileException;
import gg.moonflower.pinwheel.api.geometry.GeometryModelData;
import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.common.model.BRModel;
import nordmods.biscuit_roll.common.model.PolyMeshAttachments;
import nordmods.biscuit_roll.common.model.PolygonlessAnimatedBone;
import nordmods.biscuit_roll.common.model.PolygonlessModel;
import org.jspecify.annotations.Nullable;

/// Server instance of {@link BRModelManager}
public class ServerModelManager extends BRModelManager {
    private static final ServerModelManager INSTANCE = new ServerModelManager();

    private ServerModelManager() {}

    public static ServerModelManager instance() {
        return INSTANCE;
    }

    @Override
    public @Nullable PolyMeshAttachments getPolymeshAttachments(Identifier modelId) { //not needed
        return null;
    }

    @Override
    protected BRModel makeModel(Identifier modelId, GeometryModelData data) throws GeometryCompileException {
        return new PolygonlessModel(data);
    }
}
