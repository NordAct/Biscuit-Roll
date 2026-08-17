package nordmods.biscuit_roll.common.animation;

import nordmods.biscuit_roll.common.animation.controller.BRAnimationController;

import java.util.Collection;
import java.util.List;

/// Biscuit Roll Animated Object or BRAnimatedObject for short
///
/// Object, that can be rendered using [nordmods.biscuit_roll.common.model.BRModel] and animated
public interface BRAnimatedObject {
    /**
     * @return collection of object's {@link BRAnimationController}
     */
    List<BRAnimationController> getAnimationControllers();
}
