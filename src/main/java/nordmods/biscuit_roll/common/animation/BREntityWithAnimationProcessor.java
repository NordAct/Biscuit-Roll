package nordmods.biscuit_roll.common.animation;

import net.minecraft.world.entity.Entity;
import nordmods.biscuit_roll.common.BRAttachments;
import nordmods.biscuit_roll.common.animation.processor.ControllerState;

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
