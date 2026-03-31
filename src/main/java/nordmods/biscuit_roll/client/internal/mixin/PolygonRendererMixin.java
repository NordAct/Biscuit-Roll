package nordmods.biscuit_roll.client.internal.mixin;

import gg.moonflower.pinwheel.api.geometry.bone.Polygon;
import gg.moonflower.pinwheel.api.transform.MatrixStack;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import nordmods.biscuit_roll.client.internal.BRModelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BRModelRenderer.PolygonRenderer.class)
public class PolygonRendererMixin {
    @Unique
    private ProfilerFiller profiler;
    @Inject(method = "render", at = @At("HEAD"))
    private void renderPolygonProfilerPush(MatrixStack matrixStack, Polygon polygon, CallbackInfo ci) {
        profiler = Profiler.get();
        profiler.push("brPolygonRenderer");
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void renderPolygonProfilerPop(MatrixStack matrixStack, Polygon polygon, CallbackInfo ci) {
        profiler.pop();
    }

}
