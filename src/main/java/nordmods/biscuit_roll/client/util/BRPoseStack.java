package nordmods.biscuit_roll.client.util;

import com.mojang.blaze3d.vertex.PoseStack;
import gg.moonflower.pinwheel.api.transform.MatrixStack;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class BRPoseStack extends PoseStack implements MatrixStack {
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
}
