package nordmods.testmod.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.AtlasRegistry;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.client.resources.model.sprite.AtlasManager;
import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.common.state.StateDataType;
import nordmods.testmod.TestMod;
import nordmods.testmod.client.renderer.*;

public class TestModClient implements ClientModInitializer {
    public static final StateDataType<Boolean> IS_DRAGON_RAINBOW = new StateDataType<>(Identifier.fromNamespaceAndPath(TestMod.MOD_ID, "is_dragon_brown"));
    public static final Identifier ANIMATED_TEXTURES_ATLAS_ID = Identifier.fromNamespaceAndPath(TestMod.MOD_ID, "animated_textures");
    public static final Identifier ANIMATED_TEXTURES_SHEET = AtlasRegistry.generateTextureLocation(ANIMATED_TEXTURES_ATLAS_ID);

    @Override
    public void onInitializeClient() {
        EntityRenderers.register(TestMod.DRONE, DroneRenderer::new);
        EntityRenderers.register(TestMod.DRAGON, DragonRenderer::new);
        EntityRenderers.register(TestMod.WATER_DRAGON, WaterDragonRenderer::new);
        EntityRenderers.register(TestMod.MESHTEST, MeshtestRenderer::new);

        // You may or may not need context depending on your use case. Most of the time, you don't
        BlockEntityRenderers.register(TestMod.DONUT_BLOCK_ENTITY, (context -> new DonutRenderer()));
        // We need special model renderer to use library's renderer
        SpecialModelRenderers.ID_MAPPER.put(Identifier.fromNamespaceAndPath(TestMod.MOD_ID, "donut"), DonutRenderer.Unbaked.MAP_CODEC);

        AtlasRegistry.register( //if we want to use animated textures, we have to create our own atlas
                new AtlasManager.AtlasConfig(
                        ANIMATED_TEXTURES_SHEET,
                        ANIMATED_TEXTURES_ATLAS_ID,
                        false
                )
        );
    }
}
