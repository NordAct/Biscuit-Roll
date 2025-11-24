package nordmods.biscuit_roll.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.server.packs.PackType;
import nordmods.biscuit_roll.BiscuitRoll;
import nordmods.biscuit_roll.util.BRAnimationManager;
import nordmods.biscuit_roll.util.BRModelManager;

//todo move from FAPI to loader agnostic approach
public class BiscuitRollClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloader(BiscuitRoll.id("model"), new BRModelManager());
        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloader(BiscuitRoll.id("animation"), new BRAnimationManager());
    }
}
