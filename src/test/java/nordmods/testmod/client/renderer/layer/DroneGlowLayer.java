package nordmods.testmod.client.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.client.renderer.BRRenderer;
import nordmods.biscuit_roll.client.renderer.TextureRenderLayer;
import nordmods.biscuit_roll.client.state.ClientStateDataTypes;
import nordmods.biscuit_roll.common.state.BRState;
import nordmods.testmod.TestMod;
import nordmods.testmod.client.TestModClient;

public class DroneGlowLayer extends TextureRenderLayer {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(TestMod.MOD_ID, "textures/drone_glow.png");

    public DroneGlowLayer(BRRenderer<?> parentRenderer) {
        super(parentRenderer);
    }

    @Override
    public Identifier getTextureId(BRState state) {
        return TEXTURE;
    }

    @Override
    public RenderType getRenderType(BRState state, Identifier texture) {
        return RenderTypes.eyes(texture);
    }

    @Override
    protected void updateRenderState(BRState state) {
        state.setStateData(ClientStateDataTypes.LIGHT, LightTexture.FULL_BRIGHT);
        state.setStateData(ClientStateDataTypes.INVISIBLE, false);
    }
}
