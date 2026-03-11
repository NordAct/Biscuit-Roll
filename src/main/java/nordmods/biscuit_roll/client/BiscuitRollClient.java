package nordmods.biscuit_roll.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.server.packs.PackType;
import nordmods.biscuit_roll.BiscuitRoll;
import nordmods.biscuit_roll.client.util.ClientAnimationManager;
import nordmods.biscuit_roll.client.util.ClientModelManager;

/// Initializer class for client side
//todo move from FAPI to loader agnostic approach
public class BiscuitRollClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(BiscuitRoll.id("model"), ClientModelManager.instance());
        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(BiscuitRoll.id("animation"), ClientAnimationManager.instance());
    }
}
