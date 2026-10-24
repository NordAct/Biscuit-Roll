package nordmods.biscuit_roll.common.animation;

import nordmods.biscuit_roll.common.animation.controller.BRAnimationController;
import nordmods.biscuit_roll.common.animation.processor.AnimationProcessor;

import java.util.LinkedList;

public interface BRAnimationProcessorOwner<T extends BRAnimationProcessorOwner<T>> extends BRAnimatedObject{
    LinkedList<BRAnimationController> getAnimationControllers(); //because of how syncronization works, controller order must be same on both sides
    AnimationProcessor<T> getAnimationProcessor();
    boolean isClient();
    default void onAnimationControllersUpdated() {}
    default T getThis() {
        return (T)this;
    }
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
    void updateControllerStates();
    float getAnimationTime();
}
