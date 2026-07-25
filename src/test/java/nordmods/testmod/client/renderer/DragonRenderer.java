package nordmods.testmod.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.client.renderer.BREntityRenderer;
import nordmods.biscuit_roll.client.util.AnimatedTextureUtil;
import nordmods.biscuit_roll.common.model.BRModel;
import nordmods.biscuit_roll.common.model.BRModelProvider;
import nordmods.biscuit_roll.common.state.BRState;
import nordmods.biscuit_roll.common.state.StateDataTypes;
import nordmods.testmod.TestMod;
import nordmods.testmod.client.TestModClient;
import nordmods.testmod.client.renderer.layer.RainbowGlowLayer;
import nordmods.testmod.common.entity.Dragon;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

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
    public @NonNull LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    public RenderType getRenderType(BRState state, Identifier texture) {
        return RenderTypes.entityCutout(texture);
    }

    @Override
    public void extractRenderState(Dragon entity, LivingEntityRenderState state, float tickDelta) {
        super.extractRenderState(entity, state, tickDelta);
        state.setStateData(TestModClient.IS_DRAGON_RAINBOW, entity.isRainbow());
    }

    @Override
    public Identifier getTextureId(BRState state) {
        return state.getStateData(TestModClient.IS_DRAGON_RAINBOW, false) ? TEXTURE_RAINBOW : TEXTURE_GREEN;
    }

    @Override
    public void adjustAnimation(BRState state, BRModel model) { //with some magic of coding, wyvern turns into amphithere
        boolean bl = state.getStateData(TestModClient.IS_DRAGON_RAINBOW, false);
        boolean isFlying = state.getStateData(StateDataTypes.CONTROLLERS).stream().anyMatch(brAnimationController -> brAnimationController.getAnimation("fly.straight") != null || brAnimationController.getAnimation("fly.idle") != null);
        if (bl) {
            if (!isFlying) { //please actually do a proper check if bone is present on the model, getBone() is nullable
                model.getBone("wing_left").getAnimationPose().rotation().add(0, 0, -90);
                model.getBone("wing_right").getAnimationPose().rotation().add(0, 0, 90);
                model.getBone("tail1").getAnimationPose().rotation().add(30, 0, 0);
                model.getBone("dragon").getAnimationPose().position().add(0, -27, 0);
            }
            model.getBone("front").getAnimationPose().position().add(0, 2, 0);
        }
        model.getBone("leg_left").setVisible(!bl);
        model.getBone("leg_right").setVisible(!bl);
    }

    @Override
    public @Nullable TextureAtlasSprite getSpriteForTexture(Identifier texture) {
        return Minecraft.getInstance().getAtlasManager().get(AnimatedTextureUtil.getSpriteIdForAtlas(TestModClient.ANIMATED_TEXTURES_TEXTURE_ID, texture));
    }
}
