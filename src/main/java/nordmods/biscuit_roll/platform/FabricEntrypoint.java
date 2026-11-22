//? if =fabric {
package nordmods.biscuit_roll.platform;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.server.packs.PackType;
import nordmods.biscuit_roll.BiscuitRoll;
import nordmods.biscuit_roll.common.networking.SendServerData;
import nordmods.biscuit_roll.common.resource_managers.ServerAnimationManager;
import nordmods.biscuit_roll.common.resource_managers.ServerModelManager;
import nordmods.biscuit_roll.common.resource_managers.ServerPolyMeshAttachmentsManager;
import nordmods.biscuit_roll.common.util.BRAttachments;

public class FabricEntrypoint implements ModInitializer {

    @Override
    public void onInitialize() {
        BiscuitRoll.commonInit();
        BRAttachments.init();

        ResourceLoader.get(PackType.SERVER_DATA).registerReloadListener(BiscuitRoll.id("poly_mesh_attachments"), ServerPolyMeshAttachmentsManager.instance());
        ResourceLoader.get(PackType.SERVER_DATA).registerReloadListener(BiscuitRoll.id("model"), ServerModelManager.instance());
        ResourceLoader.get(PackType.SERVER_DATA).registerReloadListener(BiscuitRoll.id("animation"), ServerAnimationManager.instance());

        PayloadTypeRegistry.clientboundConfiguration().register(SendServerData.TYPE, SendServerData.STREAM_CODEC);

        ServerConfigurationConnectionEvents.CONFIGURE.register((listener, _) -> {
            if (ServerConfigurationNetworking.canSend(listener, SendServerData.TYPE)) {
                listener.send(new ClientboundCustomPayloadPacket(new SendServerData()));
            }
        });
    }

}
//?}