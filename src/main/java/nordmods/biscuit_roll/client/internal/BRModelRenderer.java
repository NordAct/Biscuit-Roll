package nordmods.biscuit_roll.client.internal;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import gg.moonflower.pinwheel.api.geometry.bone.Polygon;
import gg.moonflower.pinwheel.api.geometry.bone.Vertex;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBakery;
import nordmods.biscuit_roll.common.model.BRModel;
import nordmods.biscuit_roll.common.state.BRState;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import java.util.*;

@ApiStatus.Internal
public class BRModelRenderer {
    private final PoseStack stack = new PoseStack();
    public void render(
            SubmitNodeCollection submitNodeCollection,
            MultiBufferSource.BufferSource bufferSource,
            OutlineBufferSource outlineBufferSource,
            MultiBufferSource.BufferSource bufferSource2
    ) {
        Storage storage = submitNodeCollection.biscuit_roll$getSubmitStorage();
        renderOpaque(bufferSource, outlineBufferSource, storage.opaque, bufferSource2);
        storage.translucent.sort(Comparator.comparingDouble(translucentModelSubmit -> -translucentModelSubmit.position().lengthSquared()));
        renderTranslucent(bufferSource, outlineBufferSource, storage.translucent, bufferSource2);
    }

    private void renderTranslucent(
            MultiBufferSource.BufferSource bufferSource,
            OutlineBufferSource outlineBufferSource,
            List<TranslucentSubmit<?>> list,
            MultiBufferSource.BufferSource bufferSource2
    ) {
        for (TranslucentSubmit<?> translucentModelSubmit : list) {
            this.renderModel(
                    translucentModelSubmit.submit(),
                    translucentModelSubmit.renderType(),
                    bufferSource.getBuffer(translucentModelSubmit.renderType()),
                    outlineBufferSource,
                    bufferSource2
            );
        }
    }

    private void renderOpaque(
            MultiBufferSource.BufferSource bufferSource,
            OutlineBufferSource outlineBufferSource,
            Map<RenderType, List<Submit<?>>> map,
            MultiBufferSource.BufferSource bufferSource2
    ) {
        for (Map.Entry<RenderType, List<Submit<?>>> entry : map.entrySet()) {
            VertexConsumer vertexConsumer = bufferSource.getBuffer(entry.getKey());

            for (Submit<?> submit : entry.getValue()) {
                this.renderModel(submit, entry.getKey(), vertexConsumer, outlineBufferSource, bufferSource2);
            }
        }
    }

    private <S extends BRState> void renderModel(
            Submit<S> submit,
            RenderType renderType,
            VertexConsumer vertexConsumer,
            OutlineBufferSource outlineBufferSource,
            MultiBufferSource.BufferSource bufferSource
    ) {
        stack.pushPose();
        stack.last().set(submit.pose());
        stack.scale(1, -1, -1);

        PoseStack.Pose pose = stack.last().copy();

        BRModel<S> model = submit.model();
        VertexConsumer vertexConsumer2 = submit.sprite() == null ? vertexConsumer : submit.sprite().wrap(vertexConsumer);
        model.animate(submit.state());

        model.render(((matrixStack, polygon) -> {
            renderPolygon(pose, polygon, vertexConsumer2, submit);
        }), stack);

        if (submit.outlineColor() != 0 && (renderType.outline().isPresent() || renderType.isOutline())) {
            outlineBufferSource.setColor(submit.outlineColor());
            VertexConsumer vertexConsumer3 = outlineBufferSource.getBuffer(renderType);
            model.render(((matrixStack, polygon) -> {
                renderPolygon(
                        pose,
                        polygon,
                        submit.sprite() == null ? vertexConsumer3 : submit.sprite().wrap(vertexConsumer3),
                        submit
                );
            }), stack);
        }

        if (submit.crumblingOverlay() != null && renderType.affectsCrumbling()) {
            VertexConsumer vertexConsumer3 = new SheetedDecalTextureGenerator(
                    bufferSource.getBuffer( ModelBakery.DESTROY_TYPES.get(submit.crumblingOverlay().progress())),
                    submit.crumblingOverlay().cameraPose(),
                    1.0F
            );
            model.render(((matrixStack, polygon) -> {
                renderPolygon(
                        pose,
                        polygon,
                        submit.sprite() == null ? vertexConsumer3 : submit.sprite().wrap(vertexConsumer3),
                        submit
                );
            }), stack);
        }

        stack.popPose();
    }

    private <S extends BRState> void renderPolygon(PoseStack.Pose pose, Polygon polygon, VertexConsumer vertexConsumer, Submit<S> submit) {
        Matrix4f matrix4f = pose.pose();
        Vector3f vector3f = new Vector3f();

        for (int i = 0; i < 4; i ++) {
            Vector3f normal = pose.transformNormal(polygon.normals()[i], vector3f);
            float normalX = normal.x();
            float normalY = normal.y();
            float normalZ = normal.z();

            Vertex vertex = polygon.vertices()[i];
            float vertexX = vertex.x();
            float vertexY = vertex.y();
            float vertexZ = vertex.z();

            Vector3f pos = matrix4f.transformPosition(vertexX, vertexY, vertexZ, vector3f);

            vertexConsumer.addVertex(
                    pos.x(), pos.y(), pos.z(),
                    submit.color(),
                    vertex.u(), vertex.v(),
                    submit.overlay(), submit.light(),
                    normalX, normalY, normalZ);
        }
    }

    public record Submit<S extends BRState>(
            PoseStack.Pose pose,
            BRModel<S> model,
            S state,
            int light,
            int overlay,
            int color,
            @Nullable TextureAtlasSprite sprite,
            int outlineColor,
            ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay
    ) {}

    public record TranslucentSubmit<S extends BRState> (
       Submit<S> submit,
       RenderType renderType,
       Vector3f position
    ) {}

    public static class Storage {
        private final Map<RenderType, List<Submit<?>>> opaque = new HashMap<>();
        private final List<TranslucentSubmit<?>> translucent = new ArrayList<>();
        private final Set<RenderType> used = new ObjectOpenHashSet<>();

        public void add(RenderType renderType, Submit<?> submit) {
            if (renderType.pipeline().getBlendFunction().isEmpty()) {
                opaque.computeIfAbsent(renderType, renderTypex -> new ArrayList<>()).add(submit);
            } else {
                Vector3f vector3f = submit.pose().pose().transformPosition(new Vector3f());
                translucent.add(new TranslucentSubmit<>(submit, renderType, vector3f));
            }
        }

        public void clear() {
            this.translucent.clear();
            for (Map.Entry<RenderType, List<Submit<?>>> entry : opaque.entrySet()) {
                List<Submit<?>> list = entry.getValue();
                if (!list.isEmpty()) {
                    used.add(entry.getKey());
                    list.clear();
                }
            }
        }

        public void endFrame() {
            opaque.keySet().removeIf(renderType -> !this.used.contains(renderType));
            used.clear();
        }
    }
}
