package nordmods.biscuit_roll.client.internal.mixin;

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

    @Inject(method = "renderModel(Lnordmods/biscuit_roll/client/internal/BRModelRenderer$Submit;)V", at = @At("HEAD"))
    private void pushRendererName(BRModelRenderer.Submit submit, CallbackInfo ci) {
        profiler = Profiler.get();
        profiler.push(submit.state().getStateData(ClientStateDataTypes.DEBUG_RENDERER_NAME, "Non-Renderer Call"));
    }

    @Inject(method = "renderModel(Lnordmods/biscuit_roll/client/internal/BRModelRenderer$Submit;)V", at = @At("TAIL"))
    private void popRendererName(BRModelRenderer.Submit submit, CallbackInfo ci) {
        profiler.pop();
    }
}
