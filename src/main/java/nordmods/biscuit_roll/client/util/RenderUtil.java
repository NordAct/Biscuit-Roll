package nordmods.biscuit_roll.client.util;

import com.mojang.blaze3d.vertex.PoseStack;
import gg.moonflower.pinwheel.api.geometry.bone.Vertex;
import nordmods.biscuit_roll.common.model.BRModel;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.function.Consumer;

public class RenderUtil {
    public static void getExtentsForGui(BRModel model, PoseStack poseStack, Consumer<Vector3fc> consumer) {
        model.render((matrixStack, polygon) -> {
            PoseStack.Pose pose = poseStack.last();
            Matrix4f matrix4f = pose.pose();
            Vector3f vector3f = new Vector3f();

            for (int i = 0; i < 4; i ++) {
                pose.transformNormal(polygon.normals()[i], vector3f);
                Vertex vertex = polygon.vertices()[i];
                float vertexX = vertex.x();
                float vertexY = vertex.y();
                float vertexZ = vertex.z();

                matrix4f.transformPosition(vertexX, vertexY, vertexZ, vector3f);
                consumer.accept(vector3f);
            }
        }, poseStack);
    }
}
