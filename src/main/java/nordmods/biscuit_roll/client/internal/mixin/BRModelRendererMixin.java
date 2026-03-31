package nordmods.biscuit_roll.client.internal.mixin;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import nordmods.biscuit_roll.client.internal.BRModelRenderer;
import nordmods.biscuit_roll.client.state.ClientStateDataTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BRModelRenderer.class)
public class BRModelRendererMixin {
    @Unique
    private ProfilerFiller profiler;
    @Inject(method = "renderSolid", at = @At("HEAD"))
    private void renderSolidProfilerPush(SubmitNodeCollection submitNodeCollection, MultiBufferSource.BufferSource bufferSource, OutlineBufferSource outlineBufferSource, MultiBufferSource.BufferSource crumblingBufferSource, CallbackInfo ci) {
        profiler = Profiler.get();
        profiler.push("brRenderSolid");
    }

    @Inject(method = "renderSolid", at = @At("TAIL"))
    private void renderSolidProfilerPop(SubmitNodeCollection submitNodeCollection, MultiBufferSource.BufferSource bufferSource, OutlineBufferSource outlineBufferSource, MultiBufferSource.BufferSource crumblingBufferSource, CallbackInfo ci) {
        profiler.pop();
    }

    @Inject(method = "renderTranslucent", at = @At("HEAD"))
    private void renderTranslucentProfilerPush(SubmitNodeCollection submitNodeCollection, MultiBufferSource.BufferSource bufferSource, OutlineBufferSource outlineBufferSource, MultiBufferSource.BufferSource crumblingBufferSource, CallbackInfo ci) {
        profiler = Profiler.get();
        profiler.push("brRenderTranslucent");
    }

    @Inject(method = "renderTranslucent", at = @At("TAIL"))
    private void renderTranslucentProfilerPop(SubmitNodeCollection submitNodeCollection, MultiBufferSource.BufferSource bufferSource, OutlineBufferSource outlineBufferSource, MultiBufferSource.BufferSource crumblingBufferSource, CallbackInfo ci) {
        profiler.pop();
    }

    @Inject(method = "renderModel(Lnordmods/biscuit_roll/client/internal/BRModelRenderer$Submit;Lnet/minecraft/client/renderer/rendertype/RenderType;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/client/renderer/OutlineBufferSource;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;)V", at = @At("HEAD"))
    private void pushRendererName(BRModelRenderer.Submit submit, RenderType renderType, VertexConsumer textureBuffer, OutlineBufferSource outlineBufferSource, MultiBufferSource.BufferSource bufferSource, CallbackInfo ci) {
        profiler = Profiler.get();
        profiler.push(submit.state().getStateData(ClientStateDataTypes.DEBUG_RENDERER_NAME, "Non-Renderer Call"));
    }

    @Inject(method = "renderModel(Lnordmods/biscuit_roll/client/internal/BRModelRenderer$Submit;Lnet/minecraft/client/renderer/rendertype/RenderType;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/client/renderer/OutlineBufferSource;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;)V", at = @At("TAIL"))
    private void popRendererName(BRModelRenderer.Submit submit, RenderType renderType, VertexConsumer textureBuffer, OutlineBufferSource outlineBufferSource, MultiBufferSource.BufferSource bufferSource, CallbackInfo ci) {
        profiler.pop();
    }
}
