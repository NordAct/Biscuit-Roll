package nordmods.biscuit_roll.client.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.client.internal.BRModelSubmitStorage;
import nordmods.biscuit_roll.client.renderer.BRRenderer;
import nordmods.biscuit_roll.common.state.BRState;
import org.jspecify.annotations.Nullable;

/// Renders texture with the same model provided via parent renderer in specified render order
public abstract class TextureRenderLayer extends BRRenderLayer {
    protected final int renderOrder;
    public TextureRenderLayer(BRRenderer<?> parentRenderer, int renderOrder) {
        super(parentRenderer);
        this.renderOrder = renderOrder;
    }

    public TextureRenderLayer(BRRenderer<?> parentRenderer) {
        this(parentRenderer, 1);
    }

    public abstract Identifier getTextureId(BRState state);

    public RenderType getRenderType(BRState state, Identifier texture) {
        return parentRenderer.getRenderType(state, texture);
    }

    @Override
    protected void submit(BRState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        Identifier texture = getTextureId(state);
        BRRenderer.submitModel(poseStack, getModel(state), state, this::getRenderType, texture, getSpriteForTexture(texture), (BRModelSubmitStorage)submitNodeCollector.order(renderOrder));
    }

    /**
     * Gets atlas sprite for given texture. By default, gets texture from same atlas as parent one
     *
     * @param texture texture identifier
     * @return atlas sprite for given texture
     * @see BRRenderer#getSpriteForTexture(Identifier)
     */
    @Nullable
    protected TextureAtlasSprite getSpriteForTexture(Identifier texture) {
        return parentRenderer.getSpriteForTexture(texture);
    }
}
