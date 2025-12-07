package nordmods.testmod.client.model_provider;

import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.common.model.BRModelProvider;
import nordmods.biscuit_roll.common.state.BRState;
import nordmods.testmod.TestMod;
import nordmods.testmod.client.TestModClient;

public class DragonModelProvider implements BRModelProvider {
    private static final Identifier MODEL = Identifier.fromNamespaceAndPath(TestMod.MOD_ID, "dragon.geo");
    private static final Identifier ANIMATION = Identifier.fromNamespaceAndPath(TestMod.MOD_ID, "dragon.animation");
    private static final Identifier TEXTURE_GREEN = Identifier.fromNamespaceAndPath(TestMod.MOD_ID, "textures/green.png");
    private static final Identifier TEXTURE_BROWN = Identifier.fromNamespaceAndPath(TestMod.MOD_ID, "textures/animated_textures/rainbow.png");

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
        return state.getStateDataOptional(TestModClient.IS_DRAGON_BROWN).orElse(false) ? TEXTURE_BROWN : TEXTURE_GREEN;
    }
}
