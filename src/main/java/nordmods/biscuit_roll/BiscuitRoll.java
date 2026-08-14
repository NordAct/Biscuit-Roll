package nordmods.biscuit_roll;

import com.mojang.logging.LogUtils;
import gg.moonflower.molangcompiler.api.MolangCompiler;
import gg.moonflower.pinwheel.api.PinwheelMolangCompiler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import nordmods.biscuit_roll.common.networking.SendServerData;
import nordmods.biscuit_roll.common.util.ServerAnimationManager;
import nordmods.biscuit_roll.common.util.ServerModelManager;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

/// Initializer class for both sides
//todo: reminder to self - change license once done
public class BiscuitRoll implements ModInitializer {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "biscuit_roll";

    @Override
    public void onInitialize() {
        PinwheelMolangCompiler.set(MolangCompiler.create(MolangCompiler.DEFAULT_FLAGS, BiscuitRoll.class.getClassLoader())); //what

        ResourceLoader.get(PackType.SERVER_DATA).registerReloadListener(BiscuitRoll.id("model"), ServerModelManager.instance());
        ResourceLoader.get(PackType.SERVER_DATA).registerReloadListener(BiscuitRoll.id("animation"), ServerAnimationManager.instance());

        PayloadTypeRegistry.clientboundConfiguration().register(SendServerData.TYPE, SendServerData.STREAM_CODEC);

        ServerConfigurationConnectionEvents.BEFORE_CONFIGURE.register((listener, server) -> {
            if (ServerConfigurationNetworking.canSend(listener, SendServerData.TYPE)) {
                listener.send(new ClientboundCustomPayloadPacket(new SendServerData()));
            }
        });
    }

    @ApiStatus.Internal
    public static Identifier id(String id) {
        return Identifier.fromNamespaceAndPath(MOD_ID, id);
    }
}
