package nordmods.biscuit_roll.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import nordmods.biscuit_roll.client.internal.BRModelRenderer;
import nordmods.biscuit_roll.client.internal.BRModelSubmitCollection;
import nordmods.biscuit_roll.client.state.ClientStateDataTypes;
import nordmods.biscuit_roll.common.model.BRModel;
import nordmods.biscuit_roll.common.state.BRState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SubmitNodeCollection.class)
public class SubmitNodeCollectionMixin implements BRModelSubmitCollection {
    @Shadow
    private boolean wasUsed;
    @Unique
    private final BRModelRenderer.Storage biscuit_roll$storage = new BRModelRenderer.Storage();
    @Override
    public BRModelRenderer.Storage biscuit_roll$getSubmitStorage() {
        return biscuit_roll$storage;
    }

    @Override
    public <S extends BRState> void biscuit_roll$submit(PoseStack.Pose pose, BRModel<S> model, S state, RenderType renderType) {
        wasUsed = true;
        biscuit_roll$storage.add(renderType,
                new BRModelRenderer.Submit<>(
                        pose,
                        model,
                        state,
                        state.getStateData(ClientStateDataTypes.LIGHT).orElse(LightTexture.FULL_BRIGHT),
                        state.getStateData(ClientStateDataTypes.OVERLAY_TEXTURE).orElse(OverlayTexture.NO_OVERLAY),
                        state.getStateData(ClientStateDataTypes.COLOR).orElse(-1),
                        state.getStateData(ClientStateDataTypes.TEXTURE_ATLAS_SPRITE).orElse(null),
                        state.getStateData(ClientStateDataTypes.OUTLINE_COLOR).orElse(0),
                        state.getStateData(ClientStateDataTypes.CRUMBLING_OVERLAY).orElse(null)
                ));
    }

    @Inject(method = "clear", at = @At("TAIL"))
    private void clearStorage(CallbackInfo ci) {
        biscuit_roll$storage.clear();
    }

    @Inject(method = "endFrame", at = @At("HEAD"))
    private void onEndFrame(CallbackInfo ci) {
        biscuit_roll$storage.endFrame();
    }
}
