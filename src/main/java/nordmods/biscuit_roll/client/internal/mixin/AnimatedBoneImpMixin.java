package nordmods.biscuit_roll.client.internal.mixin;

import gg.moonflower.pinwheel.api.geometry.GeometryRenderer;
import gg.moonflower.pinwheel.api.transform.MatrixStack;
import gg.moonflower.pinwheel.impl.geometry.bone.AnimatedBoneImpl;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Debug(export = true)
@Mixin(AnimatedBoneImpl.class)
public class AnimatedBoneImpMixin {
    @Unique
    private ProfilerFiller profiler;

    @Inject(method = "render", at = @At("HEAD"))
    private void renderProfilerPush(GeometryRenderer renderer, MatrixStack matrixStack, CallbackInfo ci) {
        profiler = Profiler.get();
        profiler.push("brRenderBone");
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void renderProfilerPop(GeometryRenderer renderer, MatrixStack matrixStack, CallbackInfo ci) {
        profiler.pop();
    }

    @Inject(method = "translateAndRotate", at = @At("HEAD"))
    private void translateAndRotateProfilerPush(MatrixStack matrixStack, CallbackInfo ci) {
        profiler = Profiler.get();
        profiler.push("brTranslateAndRotate");
    }

    @Inject(method = "translateAndRotate", at = @At("TAIL"))
    private void translateAndRotateProfilerPop(MatrixStack matrixStack, CallbackInfo ci) {
        profiler.pop();
    }
}
