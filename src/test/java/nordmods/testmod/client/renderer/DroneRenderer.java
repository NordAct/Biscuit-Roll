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
import nordmods.testmod.client.renderer.layer.DroneGlowLayer;
import nordmods.testmod.common.entity.Drone;
import org.jspecify.annotations.NonNull;

public class DroneRenderer extends BREntityRenderer<Drone, LivingEntityRenderState> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(TestMod.MOD_ID, "textures/drone.png");
    private static final Identifier MODEL = Identifier.fromNamespaceAndPath(TestMod.MOD_ID, "biscuit_roll/models/drone.geo.json");
    private static final Identifier ANIMATION = Identifier.fromNamespaceAndPath(TestMod.MOD_ID, "biscuit_roll/animations/drone.animation.json");

    public DroneRenderer(EntityRendererProvider.Context context) {
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
        addRenderLayer(new DroneGlowLayer(this));
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
    public Identifier getTextureId(BRState state) {
        return TEXTURE;
    }
}
