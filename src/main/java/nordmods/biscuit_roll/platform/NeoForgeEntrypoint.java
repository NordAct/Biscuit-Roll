//? if =neoforge {
/*package nordmods.biscuit_roll.platform;

import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.event.RegisterConfigurationTasksEvent;
import nordmods.biscuit_roll.BiscuitRoll;
import nordmods.biscuit_roll.common.networking.SendServerData;
import nordmods.biscuit_roll.common.resource_managers.ServerAnimationManager;
import nordmods.biscuit_roll.common.resource_managers.ServerModelManager;
import nordmods.biscuit_roll.common.resource_managers.ServerPolyMeshAttachmentsManager;
import nordmods.biscuit_roll.common.util.BRAttachments;

@Mod(BiscuitRoll.MOD_ID)
@EventBusSubscriber
public class NeoForgeEntrypoint {
    public NeoForgeEntrypoint(IEventBus modBus) {
        BiscuitRoll.commonInit();
        BRAttachments.init(modBus);
    }

    @SubscribeEvent
    public static void addServerReloadListeners(AddServerReloadListenersEvent event) {
        event.addListener(BiscuitRoll.id("poly_mesh_attachments"), ServerPolyMeshAttachmentsManager.instance());
        event.addListener(BiscuitRoll.id("model"), ServerModelManager.instance());
        event.addListener(BiscuitRoll.id("animation"), ServerAnimationManager.instance());
    }

    @SubscribeEvent
    public static void registerPayload(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.configurationToClient(SendServerData.TYPE, SendServerData.STREAM_CODEC);
    }

    @SubscribeEvent
    public static void addConfigurationListener(RegisterConfigurationTasksEvent event) {
        event.getListener().send(new ClientboundCustomPayloadPacket(new SendServerData()));
    }
}
*///?}
