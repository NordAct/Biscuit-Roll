package nordmods.testmod.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import nordmods.testmod.TestMod;

public class TestModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRenderers.register(TestMod.DRONE, DroneRenderer::new);
    }
}
