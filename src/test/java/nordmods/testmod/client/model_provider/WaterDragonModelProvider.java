package nordmods.testmod.client.model_provider;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.common.model.BRModelProvider;
import nordmods.biscuit_roll.common.state.BRState;
import nordmods.testmod.TestMod;

public class WaterDragonModelProvider implements BRModelProvider {
    private static final Identifier MODEL = Identifier.fromNamespaceAndPath(TestMod.MOD_ID, "water_dragon.geo");
    private static final Identifier ANIMATION = Identifier.fromNamespaceAndPath(TestMod.MOD_ID, "water_dragon.animation");
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(TestMod.MOD_ID, "textures/water_dragon.png");

    @Override
    public Identifier getModelId(BRState state) {
        return MODEL;
    }

    @Override
    public Identifier getAnimationId(BRState state) {
        return ANIMATION;
    }

    @Override
    public Identifier getTextureId(BRState state) {
        return TEXTURE;
    }
}
