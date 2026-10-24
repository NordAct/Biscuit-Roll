package nordmods.biscuit_roll.mixin.common;

import net.minecraft.world.entity.Entity;
import nordmods.biscuit_roll.common.animation.BREntityWithAnimationProcessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V"))
    private void updateControllers(CallbackInfo ci) {
        if (this instanceof BREntityWithAnimationProcessor<?> processorOwner)
            processorOwner.tickAnimationProcessor();
    }
}
