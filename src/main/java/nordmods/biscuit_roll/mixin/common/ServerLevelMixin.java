package nordmods.biscuit_roll.mixin.common;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import nordmods.biscuit_roll.common.animation.BREntityWithAnimationProcessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {
    @Inject(method = "tickNonPassenger", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;tick()V", shift = At.Shift.AFTER))
    private void updateControllers(CallbackInfo ci, @Local Entity entity) {
        if (entity instanceof BREntityWithAnimationProcessor<?> processorOwner)
            processorOwner.tickAnimationProcessor();
    }
    @Inject(method = "tickPassenger", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;rideTick()V", shift = At.Shift.AFTER))
    private void updateControllersRidden(CallbackInfo ci, @Local(ordinal = 1) Entity entity) {
        if (entity instanceof BREntityWithAnimationProcessor<?> processorOwner)
            processorOwner.tickAnimationProcessor();
    }
}
