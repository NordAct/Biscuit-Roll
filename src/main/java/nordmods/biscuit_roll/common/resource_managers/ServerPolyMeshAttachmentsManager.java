package nordmods.biscuit_roll.common.resource_managers;

import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.common.model.PolyMeshAttachments;

import java.util.HashMap;
import java.util.Map;

/// Server instance of {@link BRPolyMeshAttachmentsManager}
/// Note: poly mesh attachments are not used on server side by default. This exists for purpose of server client synchronization
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
