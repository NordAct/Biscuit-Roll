package nordmods.biscuit_roll.common.resource_managers;

import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.common.model.PolyMeshAttachments;

import java.util.HashMap;
import java.util.Map;

public class ServerPolyMeshAttachmentsManager extends BRPolyMeshAttachmentsManager {
    private static final ServerPolyMeshAttachmentsManager INSTANCE = new ServerPolyMeshAttachmentsManager();

    private ServerPolyMeshAttachmentsManager() {}

    private final Map<Identifier, PolyMeshAttachments> attachmentsMap = new HashMap<>();
    @Override
    public Map<Identifier, PolyMeshAttachments> getRegistry() {
        return attachmentsMap;
    }

    public static ServerPolyMeshAttachmentsManager instance() {
        return INSTANCE;
    }
}
