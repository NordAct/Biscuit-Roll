package nordmods.biscuit_roll.client.internal;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import gg.moonflower.pinwheel.api.geometry.GeometryRenderer;
import gg.moonflower.pinwheel.api.geometry.bone.Polygon;
import gg.moonflower.pinwheel.api.geometry.bone.Vertex;
import gg.moonflower.pinwheel.api.transform.MatrixStack;
import net.minecraft.client.renderer.feature.FeatureFrameContext;
import net.minecraft.client.renderer.feature.FeatureRendererType;
import net.minecraft.client.renderer.feature.RenderTypeFeatureRenderer;
import net.minecraft.client.renderer.feature.submit.BatchableSubmit;
import net.minecraft.client.renderer.feature.submit.TranslucentSubmit;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import nordmods.biscuit_roll.common.model.BRModel;
import nordmods.biscuit_roll.common.state.BRState;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

/// it renders
@ApiStatus.Internal
public class BRModelRenderer extends RenderTypeFeatureRenderer<BRModelRenderer.Submit> {
    public static final FeatureRendererType<Submit> TYPE = FeatureRendererType.create("Biscuit Roll Model");
    private final PoseStack stack = new PoseStack();
    private final PolygonRenderer polygonRenderer = new PolygonRenderer();

    private void renderModel(Submit submit) {
        stack.pushPose();
        stack.last().set(submit.pose());

        VertexConsumer buffer = this.getVertexBuilder(submit.renderType());
        if (submit.sheetedDecalPose() != null) buffer = new SheetedDecalTextureGenerator(buffer, submit.sheetedDecalPose(), 1);
        else if (submit.sprite() != null) buffer = submit.sprite().wrap(buffer);

        submit.model.applyAnimations(submit.state);
        renderModel(submit.model, stack, buffer, submit.tintedColor, submit.overlayCoords, submit.lightCoords);

        stack.popPose();
    }

    private void renderModel(BRModel model, PoseStack stack, VertexConsumer vertexConsumer, int color, int overlayTexture, int light) {
        polygonRenderer.color = color;
        polygonRenderer.overlayTexture = overlayTexture;
        polygonRenderer.light = light;
        polygonRenderer.vertexConsumer = vertexConsumer;
        model.render(polygonRenderer, stack);
    }

    @Override
    protected void buildGroup(@NonNull FeatureFrameContext context, @NonNull List<Submit> submits) {
        submits.forEach(this::renderModel);
    }

    public record Submit(
            RenderType renderType,
            PoseStack.Pose pose,
            BRModel model,
            BRState state,
            int lightCoords,
            int overlayCoords,
            int tintedColor,
            @Nullable TextureAtlasSprite sprite,
            PoseStack.@Nullable Pose sheetedDecalPose
    ) implements BatchableSubmit, TranslucentSubmit {
        @Override
        public @NonNull Object batchKey() {
            return this.renderType;
        }

        @Override
        public float distanceToCameraSq() {
            return TranslucentSubmit.computeDistanceToCameraSq(this.pose.pose());
        }

        @Override
        public @NonNull FeatureRendererType<Submit> featureType() {
            return TYPE;
        }
    }

    public static class PolygonRenderer implements GeometryRenderer {
        private int color;
        private int overlayTexture;
        private int light;
        private VertexConsumer vertexConsumer;

        @Override
        public void render(MatrixStack matrixStack, Polygon polygon) {
            Vector3f vector3f = new Vector3f();

            for (int i = 0; i < polygon.vertices().length; i ++) {
                Vector3f normal = ((PoseStack)matrixStack).last().transformNormal(polygon.normals()[i], vector3f);
                float normalX = normal.x();
                float normalY = normal.y();
                float normalZ = normal.z();

                Vertex vertex = polygon.vertices()[i];
                float vertexX = vertex.x();
                float vertexY = vertex.y();
                float vertexZ = vertex.z();

                Vector3f pos = matrixStack.position().transformPosition(vertexX, vertexY, vertexZ, vector3f);

                vertexConsumer.addVertex(
                        pos.x(), pos.y(), pos.z(),
                        color,
                        vertex.u(), vertex.v(),
                        overlayTexture,
                        light,
                        normalX, normalY, normalZ);
            }
        }
    }
}
