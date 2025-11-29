package nordmods.biscuit_roll.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import nordmods.biscuit_roll.client.internal.BRModelRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FeatureRenderDispatcher.class)
public class FeatureRenderDispatcherMixin {
    @Shadow
    @Final
    private MultiBufferSource.BufferSource bufferSource;
    @Shadow
    @Final
    private OutlineBufferSource outlineBufferSource;
    @Shadow
    @Final
    private MultiBufferSource.BufferSource crumblingBufferSource;
    @Unique
    private final BRModelRenderer biscuit_roll$modelRenderer = new BRModelRenderer();

    @Inject(method = "renderAllFeatures", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/feature/ModelPartFeatureRenderer;render(Lnet/minecraft/client/renderer/SubmitNodeCollection;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;Lnet/minecraft/client/renderer/OutlineBufferSource;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;)V"))
    private void renderBRModels(CallbackInfo ci, @Local SubmitNodeCollection submitNodeCollection) {
        biscuit_roll$modelRenderer.render(submitNodeCollection, bufferSource, outlineBufferSource, crumblingBufferSource);
    }
}
