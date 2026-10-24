package nordmods.biscuit_roll.common.animation;

import net.minecraft.world.level.block.entity.BlockEntity;
import nordmods.biscuit_roll.common.BRAttachments;
import nordmods.biscuit_roll.common.animation.processor.ControllerState;

public interface BRBlockEntityWithAnimationProcessor<T extends BlockEntity & BRAnimationProcessorOwner<T>> extends BRAnimationProcessorOwner<T> {
    @Override
    default boolean isClient() {
        return getThis().hasLevel() ? getThis().getLevel().isClientSide() : false;
    }

    @Override
    default void updateControllerStates() {
        getThis().setAttached(BRAttachments.CONTROLLER_STATES, ControllerState.collectControllerStates(getAnimationControllers()));
    }
}
