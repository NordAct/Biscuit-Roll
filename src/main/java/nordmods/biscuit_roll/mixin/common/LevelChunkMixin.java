package nordmods.biscuit_roll.mixin.common;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import nordmods.biscuit_roll.common.animation.BRBlockEntityWithAnimationProcessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelChunk.BoundTickingBlockEntity.class)
public class LevelChunkMixin<T extends BlockEntity> {
    @Shadow
    @Final
    private T blockEntity;

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BlockEntityTicker;tick(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/entity/BlockEntity;)V", shift = At.Shift.AFTER))
    private void updateControllers(CallbackInfo ci) {
        if (blockEntity instanceof BRBlockEntityWithAnimationProcessor<?> processorOwner)
            processorOwner.tickAnimationProcessor();
    }
}
