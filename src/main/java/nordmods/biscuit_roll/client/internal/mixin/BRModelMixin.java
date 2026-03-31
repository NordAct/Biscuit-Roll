package nordmods.biscuit_roll.client.internal.mixin;

import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import nordmods.biscuit_roll.common.model.BRModel;
import nordmods.biscuit_roll.common.state.BRState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BRModel.class)
public abstract class BRModelMixin {
    @Unique
    private ProfilerFiller profiler;

    @Inject(method = "applyAnimations(Lnordmods/biscuit_roll/common/state/BRState;)V", at = @At("HEAD"))
    private void applyAnimationsPush(BRState state, CallbackInfo ci) {
        profiler = Profiler.get();
        profiler.push("brModelAnimation");
    }

    @Inject(method = "applyAnimations(Lnordmods/biscuit_roll/common/state/BRState;)V", at = @At("HEAD"))
    private void applyAnimationsPop(BRState state, CallbackInfo ci) {
        profiler.pop();
    }
}
