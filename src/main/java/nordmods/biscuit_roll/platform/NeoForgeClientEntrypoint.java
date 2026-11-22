//? if =neoforge {
/*package nordmods.biscuit_roll.platform;

import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import nordmods.biscuit_roll.BiscuitRoll;
import nordmods.biscuit_roll.client.resource_managers.ClientAnimationManager;
import nordmods.biscuit_roll.client.resource_managers.ClientModelManager;
import nordmods.biscuit_roll.client.resource_managers.ClientPolyMeshAttachmentsManager;
import nordmods.biscuit_roll.common.networking.SendServerData;
import nordmods.biscuit_roll.common.resource_managers.ServerAnimationManager;
import nordmods.biscuit_roll.common.resource_managers.ServerModelManager;
import nordmods.biscuit_roll.common.resource_managers.ServerPolyMeshAttachmentsManager;

@EventBusSubscriber(value = Dist.CLIENT)
public class NeoForgeClientEntrypoint {
    @SubscribeEvent
    public static void addClientReloadListeners(AddClientReloadListenersEvent event) {
        event.addListener(BiscuitRoll.id("poly_mesh_attachments"), ClientPolyMeshAttachmentsManager.instance());
        event.addListener(BiscuitRoll.id("model"), ClientModelManager.instance());
        event.addListener(BiscuitRoll.id("animation"), ClientAnimationManager.instance());
    }

    @SubscribeEvent
    public static void register(RegisterClientPayloadHandlersEvent event) {
        event.register(SendServerData.TYPE, ((payload, _) -> {
            ServerModelManager.instance().getRegistry().clear();
            ServerModelManager.instance().getRegistryRaw().clear();
            ServerModelManager.instance().getRegistryRaw().putAll(payload.modelData());

            ServerAnimationManager.instance().getRegistry().clear();
            ServerAnimationManager.instance().getRegistry().putAll(payload.animationData());

            ServerPolyMeshAttachmentsManager.instance().getRegistry().clear();
            ServerPolyMeshAttachmentsManager.instance().getRegistry().putAll(payload.polyMeshAttachments());

            ClientModelManager.instance().clearCache();
            ClientAnimationManager.instance().clearCache();
        }));
    }
}
*///?}
