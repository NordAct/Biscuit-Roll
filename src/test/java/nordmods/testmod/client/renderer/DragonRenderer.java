package nordmods.testmod.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.client.renderer.BREntityRenderer;
import nordmods.biscuit_roll.common.model.BRModelProvider;
import nordmods.biscuit_roll.common.state.BRState;
import nordmods.testmod.TestMod;
import nordmods.testmod.client.TestModClient;
import nordmods.testmod.client.renderer.layer.RainbowGlowLayer;
import nordmods.testmod.common.Dragon;
import org.jetbrains.annotations.NotNull;

public class DragonRenderer extends BREntityRenderer<Dragon, LivingEntityRenderState> {
    private static final Identifier MODEL = Identifier.fromNamespaceAndPath(TestMod.MOD_ID, "biscuit_roll/models/dragon.geo.json");
    private static final Identifier ANIMATION = Identifier.fromNamespaceAndPath(TestMod.MOD_ID, "biscuit_roll/animations/dragon.animation.json");
    private static final Identifier TEXTURE_GREEN = Identifier.fromNamespaceAndPath(TestMod.MOD_ID, "textures/green.png");
    private static final Identifier TEXTURE_RAINBOW = Identifier.fromNamespaceAndPath(TestMod.MOD_ID, "textures/animated_textures/rainbow.png");

    public DragonRenderer(EntityRendererProvider.Context context) {
        super(context, new BRModelProvider() {
            @Override
            public Identifier getModelId(BRState state) {
                return MODEL;
            }

            @Override
            public Identifier getAnimationId(BRState state) {
                return ANIMATION;
            }
        });
        addRenderLayer(new RainbowGlowLayer(this));
    }

    @Override
    public @NotNull LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    public RenderType getRenderType(BRState state, Identifier texture) {
        return RenderTypes.entityCutoutNoCull(texture);
    }

    @Override
    public void extractRenderState(Dragon entity, LivingEntityRenderState state, float tickDelta) {
        super.extractRenderState(entity, state, tickDelta);
        state.setStateData(TestModClient.IS_DRAGON_RAINBOW, entity.isRainbow());
    }

    @Override
    public Identifier getTextureId(BRState state) {
        return state.getStateDataOptional(TestModClient.IS_DRAGON_RAINBOW).orElse(false) ? TEXTURE_RAINBOW : TEXTURE_GREEN;
    }
}
