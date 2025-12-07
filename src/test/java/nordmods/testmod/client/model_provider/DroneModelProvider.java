package nordmods.testmod.client.model_provider;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.common.model.BRModelProvider;
import nordmods.biscuit_roll.common.state.BRState;
import nordmods.testmod.TestMod;

public class DroneModelProvider implements BRModelProvider {
    private static final Identifier MODEL = Identifier.fromNamespaceAndPath(TestMod.MOD_ID, "drone.geo");
    private static final Identifier ANIMATION = Identifier.fromNamespaceAndPath(TestMod.MOD_ID, "drone.animation");
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(TestMod.MOD_ID, "textures/drone.png");

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
