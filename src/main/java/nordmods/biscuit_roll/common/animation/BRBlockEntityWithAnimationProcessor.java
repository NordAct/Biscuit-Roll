package nordmods.biscuit_roll.common.animation;

import net.minecraft.world.level.block.entity.BlockEntity;
import nordmods.biscuit_roll.common.util.BRAttachments;
import nordmods.biscuit_roll.common.animation.processor.ControllerState;

/// Implementation of {@link BRAnimationProcessorOwner} for {@link BlockEntity}.
/// Controller state synchronization is made vid data attachments
/// @param <T> animated block entity
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
