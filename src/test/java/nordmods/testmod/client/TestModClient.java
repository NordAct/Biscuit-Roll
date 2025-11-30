package nordmods.testmod.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.common.state.StateDataType;
import nordmods.testmod.TestMod;
import nordmods.testmod.client.renderer.DragonRenderer;
import nordmods.testmod.client.renderer.DroneRenderer;

public class TestModClient implements ClientModInitializer {
    public static final StateDataType<Boolean> IS_DRAGON_BROWN = new StateDataType<>(Identifier.fromNamespaceAndPath(TestMod.MOD_ID, "is_dragon_brown"));

    @Override
    public void onInitializeClient() {
        EntityRenderers.register(TestMod.DRONE, DroneRenderer::new);
        EntityRenderers.register(TestMod.DRAGON, DragonRenderer::new);
    }
}
