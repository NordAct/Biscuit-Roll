package nordmods.biscuit_roll.client.internal;

import gg.moonflower.pinwheel.api.transform.MatrixStack;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

/// This interface exists solely for a reason of shadow jar not working well with Fabric Loom's interface injects
public interface BRMatrixStack extends MatrixStack {
    @Override
    default void reset() {
        throw new AssertionError("Implemented in mixin");
    }

    @Override
    default void pushMatrix() {
        throw new AssertionError("Implemented in mixin");
    }

    @Override
    default void popMatrix() {
        throw new AssertionError("Implemented in mixin");
    }

    @Override
    default Matrix4f position() {
        throw new AssertionError("Implemented in mixin");
    }

    @Override
    default Matrix3f normal() {
        throw new AssertionError("Implemented in mixin");
    }
}
