package nordmods.testmod.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.common.state.StateDataType;
import nordmods.testmod.TestMod;
import nordmods.testmod.client.renderer.DonutRenderer;
import nordmods.testmod.client.renderer.DragonRenderer;
import nordmods.testmod.client.renderer.DroneRenderer;
import nordmods.testmod.client.renderer.WaterDragonRenderer;

public class TestModClient implements ClientModInitializer {
    public static final StateDataType<Boolean> IS_DRAGON_RAINBOW = new StateDataType<>(Identifier.fromNamespaceAndPath(TestMod.MOD_ID, "is_dragon_brown"));

    @Override
    public void onInitializeClient() {
        EntityRenderers.register(TestMod.DRONE, DroneRenderer::new);
        EntityRenderers.register(TestMod.DRAGON, DragonRenderer::new);
        EntityRenderers.register(TestMod.WATER_DRAGON, WaterDragonRenderer::new);

        // You may or may not need context depending on your use case. Most of the time, you don't
        BlockEntityRenderers.register(TestMod.DONUT_BLOCK_ENTITY, (context -> new DonutRenderer()));
        // We need special model renderer to use library's renderer
        SpecialModelRenderers.ID_MAPPER.put(Identifier.fromNamespaceAndPath(TestMod.MOD_ID, "donut"), DonutRenderer.Unbaked.MAP_CODEC);
    }
}
