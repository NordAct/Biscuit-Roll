package nordmods.biscuit_roll.common.animation;

import net.minecraft.world.entity.Entity;
import nordmods.biscuit_roll.common.util.BRAttachments;
import nordmods.biscuit_roll.common.animation.processor.ControllerState;

/// Implementation of {@link BRAnimationProcessorOwner} for {@link Entity}.
/// Controller state synchronization is made vid data attachments
/// @param <T> animated entity
public interface BREntityWithAnimationProcessor<T extends Entity & BRAnimationProcessorOwner<T>> extends BRAnimationProcessorOwner<T>{
    @Override
    default boolean isClient() {
        return getThis().level().isClientSide();
    }

    @Override
    default void updateControllerStates() {
        getThis().setAttached(BRAttachments.CONTROLLER_STATES, ControllerState.collectControllerStates(getAnimationControllers()));
    }

    @Override
    default float getAnimationTime() {
        return getThis().tickCount / 20f;
    }
}
