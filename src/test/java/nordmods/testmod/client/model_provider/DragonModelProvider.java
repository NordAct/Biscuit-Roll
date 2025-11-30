package nordmods.testmod.client.model_provider;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.common.model.BRModelProvider;
import nordmods.testmod.TestMod;
import nordmods.testmod.client.TestModClient;

public class DragonModelProvider implements BRModelProvider<LivingEntityRenderState> {
    private static final Identifier MODEL = Identifier.fromNamespaceAndPath(TestMod.MOD_ID, "dragon.geo");
    private static final Identifier ANIMATION = Identifier.fromNamespaceAndPath(TestMod.MOD_ID, "drone.animation");
    private static final Identifier TEXTURE_GREEN = Identifier.fromNamespaceAndPath(TestMod.MOD_ID, "textures/dragon_green.png");
    private static final Identifier TEXTURE_BROWN = Identifier.fromNamespaceAndPath(TestMod.MOD_ID, "textures/dragon_brown.png");

    @Override
    public Identifier getModelId(LivingEntityRenderState state) {
        return MODEL;
    }

    @Override
    public Identifier getAnimationId(LivingEntityRenderState state) {
        return ANIMATION;
    }

    @Override
    public Identifier getTextureId(LivingEntityRenderState state) {
        return state.getStateData(TestModClient.IS_DRAGON_BROWN).orElse(false) ? TEXTURE_BROWN : TEXTURE_GREEN;
    }
}
