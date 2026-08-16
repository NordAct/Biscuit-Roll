package nordmods.biscuit_roll.client.resource_managers;

import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.common.model.PolyMeshAttachments;
import nordmods.biscuit_roll.common.resource_managers.BRPolyMeshAttachmentsManager;

import java.util.HashMap;
import java.util.Map;

public class ClientPolyMeshAttachmentsManager extends BRPolyMeshAttachmentsManager {
    private static final ClientPolyMeshAttachmentsManager INSTANCE = new ClientPolyMeshAttachmentsManager();

    private ClientPolyMeshAttachmentsManager() {}

    private final Map<Identifier, PolyMeshAttachments> attachmentsMap = new HashMap<>();
    @Override
    public Map<Identifier, PolyMeshAttachments> getRegistry() {
        return attachmentsMap;
    }

    public static ClientPolyMeshAttachmentsManager instance() {
        return INSTANCE;
    }
}
