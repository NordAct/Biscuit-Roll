package nordmods.biscuit_roll.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import gg.moonflower.pinwheel.api.transform.MatrixStack;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionfc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PoseStack.class)
public abstract class PoseStackMixin implements MatrixStack {
    @Shadow
    public abstract boolean isEmpty();

    @Shadow
    public abstract void popPose();

    @Shadow
    public abstract void setIdentity();

    @Shadow
    public abstract void pushPose();

    @Shadow
    public abstract PoseStack.Pose last();

    @Override
    public void reset() {
        while (isEmpty()) {
            popPose();
        }
        setIdentity();
    }

    @Override
    public void pushMatrix() {
        pushPose();
    }

    @Override
    public void popMatrix() {
        popPose();
    }

    @Override
    public Matrix4f position() {
        return last().pose();
    }

    @Override
    public Matrix3f normal() {
        return last().normal();
    }

    @Override
    public void rotate(Quaternionfc rotation) {
        MatrixStack.super.rotate(rotation);
        this.normal().rotate(rotation);
    }

    @Override
    public void rotate(float amount, float x, float y, float z) {
        MatrixStack.super.rotate(amount, x, y, z);
        this.normal().rotate(amount, x, y, z);
    }

    @Override
    public void rotateXYZ(float x, float y, float z) {
        MatrixStack.super.rotateXYZ(x, y, z);
        this.normal().rotateXYZ(x, y, z);
    }

    @Override
    public void rotateZYX(float z, float y, float x) {
        MatrixStack.super.rotateZYX(z, y, x);
        this.normal().rotateZYX(z, y, x);
    }

    @Override
    public void copy(MatrixStack stack) {
        MatrixStack.super.copy(stack);
        this.normal().set(stack.normal());
    }
}
