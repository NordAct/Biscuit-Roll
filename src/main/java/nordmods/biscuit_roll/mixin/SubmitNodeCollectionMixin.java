package nordmods.biscuit_roll.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.feature.phase.SimpleFeatureRenderPhase;
import net.minecraft.client.renderer.feature.phase.TranslucentFeatureRenderPhase;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import nordmods.biscuit_roll.client.internal.BRModelRenderer;
import nordmods.biscuit_roll.client.internal.BRModelSubmitStorage;
import nordmods.biscuit_roll.client.state.ClientStateDataTypes;
import nordmods.biscuit_roll.client.util.AnimatedTextureUtil;
import nordmods.biscuit_roll.common.model.BRModel;
import nordmods.biscuit_roll.common.state.BRState;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SubmitNodeCollection.class)
public class SubmitNodeCollectionMixin implements BRModelSubmitStorage{

    @Shadow
    @Final
    public SimpleFeatureRenderPhase waterMask;

    @Shadow
    @Final
    public TranslucentFeatureRenderPhase translucentModels;

    @Shadow
    @Final
    public SimpleFeatureRenderPhase solid;

    @Shadow
    private static @Nullable RenderType getOutlineRenderType(RenderType renderType) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    @Shadow
    @Final
    public SimpleFeatureRenderPhase outline;

    @Shadow
    @Final
    public SimpleFeatureRenderPhase breakingOverlay;

    @Override
    public void biscuit_roll$submit(
            PoseStack.Pose pose,
            BRModel model,
            BRState state,
            BRModelSubmitStorage.RenderTypeProvider renderTypeProvider,
            Identifier texture
    ) {
        TextureAtlasSprite sprite = AnimatedTextureUtil.getAnimatedTextureSprite(texture);
        RenderType renderType = renderTypeProvider.getRenderType(state, sprite == null ? texture : sprite.atlasLocation());
        int color = state.getStateData(ClientStateDataTypes.COLOR, -1);
        int overlayTexture = state.getStateData(ClientStateDataTypes.OVERLAY_TEXTURE, OverlayTexture.NO_OVERLAY);
        int light = state.getStateData(ClientStateDataTypes.LIGHT, LightCoordsUtil.FULL_BRIGHT);
        boolean invisible = state.getStateData(ClientStateDataTypes.INVISIBLE, false);
        if (!renderType.isOutline() && !invisible) {
            BRModelRenderer.Submit submit = new BRModelRenderer.Submit(renderType, pose, model, state, light, overlayTexture, color, sprite, null);
            if (renderType == RenderTypes.waterMask()) waterMask.submit(submit);
            else if (renderType.hasBlending()) translucentModels.submit(submit);
            else solid.submit(submit);
        }

        int outlineColor = state.getStateData(ClientStateDataTypes.OUTLINE_COLOR, 0);
        if (outlineColor != 0) {
            RenderType outlineRenderType = getOutlineRenderType(renderType);
            if (outlineRenderType != null) {
                outline.submit(new BRModelRenderer.Submit(outlineRenderType, pose, model, state, light, overlayTexture, color, sprite, null));
            }
        }

        ModelFeatureRenderer.CrumblingOverlay crumblingOverlay = state.getStateData(ClientStateDataTypes.CRUMBLING_OVERLAY);
        if (crumblingOverlay != null && renderType.affectsCrumbling()) {
            RenderType crumblingRenderType = ModelBakery.DESTROY_TYPES.get(crumblingOverlay.progress());
            this.breakingOverlay.submit(new BRModelRenderer.Submit(crumblingRenderType, pose, model, state, light, overlayTexture, color,null, crumblingOverlay.cameraPose()));
        }
    }
}
