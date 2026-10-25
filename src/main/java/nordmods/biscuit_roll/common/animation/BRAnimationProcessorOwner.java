package nordmods.biscuit_roll.common.animation;

import nordmods.biscuit_roll.common.animation.controller.BRAnimationController;
import nordmods.biscuit_roll.common.animation.processor.AnimationProcessor;
import org.jetbrains.annotations.ApiStatus;

import java.util.LinkedList;

/// Base interface for animated objects that can be processed serverside.
/// Uses {@link nordmods.biscuit_roll.common.animation.processor.ControllerState} for synchronization with client
///
/// Currently supported implementations out of the box by library are {@link BREntityWithAnimationProcessor} and {@link BRBlockEntityWithAnimationProcessor}
/// @param <T> animated object
public interface BRAnimationProcessorOwner<T extends BRAnimationProcessorOwner<T>> extends BRAnimatedObject{
    /// @return collection of animation controllers in linked list to preserve the order between server and client sides
    LinkedList<BRAnimationController> getAnimationControllers();

    /// @return animation processor of the object
    AnimationProcessor<T> getAnimationProcessor();

    /// @return if object exists clientside or serverside
    boolean isClient();

    /// Called on client side after object receives and applies synchronized controller states
    default void onAnimationControllersUpdated() {}

    @ApiStatus.Internal
    default T getThis() {
        return (T)this;
    }

    /// @return if animation processor should tick on client side too
    default boolean tickAnimationProcessorOnClient() {
        return false;
    }
    default void tickAnimationProcessor() {
        if (!isClient()) {
            getAnimationProcessor().tick();
            updateControllerStates();
        } else {
            if (tickAnimationProcessorOnClient()) getAnimationProcessor().tick();
        }
    }

    /// Synchronizes controller states with client
    void updateControllerStates();

    /// @return animated object's reference animation time in seconds
    float getAnimationTime();
}
