package nordmods.biscuit_roll.client.internal.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import nordmods.biscuit_roll.client.internal.BRModelSubmitStorage;
import nordmods.biscuit_roll.client.renderer.BRRenderer;
import nordmods.biscuit_roll.client.state.ClientStateDataTypes;
import nordmods.biscuit_roll.common.state.BRState;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BRRenderer.class)
public interface BRRenererMixin<S extends BRState> {
    @Inject(method = "submit", at = @At("HEAD"))
    private void rememberRendererName(S state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, @Nullable CameraRenderState cameraRenderState, BRModelSubmitStorage brModelSubmitStorage, CallbackInfo ci) {
        state.setStateData(ClientStateDataTypes.DEBUG_RENDERER_NAME,((BRRenderer<S>)this).debugName());
    }
}
