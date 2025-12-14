package nordmods.biscuit_roll.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.client.internal.BRModelSubmitStorage;
import nordmods.biscuit_roll.client.util.ClientModelManager;
import nordmods.biscuit_roll.common.model.BRModel;
import nordmods.biscuit_roll.common.state.BRState;
import org.jetbrains.annotations.Nullable;

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
        BRRenderer.submitModel(poseStack, getModel(state), state, this::getRenderType, texture, (BRModelSubmitStorage)submitNodeCollector.order(renderOrder));
    }

    @Nullable
    private BRModel getModel(BRState state) {
        return ClientModelManager.instance().getModel(parentRenderer.getModelProvider().getModelId(state));
    }
}
