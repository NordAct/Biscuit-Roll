package nordmods.biscuit_roll.client.internal;

import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import gg.moonflower.pinwheel.api.geometry.bone.Polygon;
import gg.moonflower.pinwheel.api.geometry.bone.Vertex;
import gg.moonflower.pinwheel.api.transform.MatrixStack;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.util.Mth;
import nordmods.biscuit_roll.model.BRModel;
import nordmods.biscuit_roll.state.BRState;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.*;

@ApiStatus.Internal
public class BRModelRenderer {
    public void render(
            SubmitNodeCollection submitNodeCollection,
            MultiBufferSource.BufferSource bufferSource,
            OutlineBufferSource outlineBufferSource,
            MultiBufferSource.BufferSource bufferSource2
    ) {
        Storage storage = ((BRModelSubmits)submitNodeCollection).biscuit_roll$getSubmitStorage();
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
        MatrixStack matrixStack = submit.matrixStack();

        matrixStack.pushMatrix();
        BRModel<S> model = submit.model();
        VertexConsumer vertexConsumer2 = submit.sprite() == null ? vertexConsumer : submit.sprite().wrap(vertexConsumer);
        model.animate(submit.state());
        model.render(((matrixStack1, polygon) -> {
            renderPolygon(matrixStack1, polygon, vertexConsumer2, submit);
        }), matrixStack);
        if (submit.outlineColor() != 0 && (renderType.outline().isPresent() || renderType.isOutline())) {
            outlineBufferSource.setColor(submit.outlineColor());
            VertexConsumer vertexConsumer3 = outlineBufferSource.getBuffer(renderType);
            model.render(((matrixStack1, polygon) -> {
                renderPolygon(
                        matrixStack1,
                        polygon,
                        submit.sprite() == null ? vertexConsumer3 : submit.sprite().wrap(vertexConsumer3),
                        submit
                );
            }), matrixStack);
        }

        if (submit.crumblingOverlay() != null && renderType.affectsCrumbling()) {
            VertexConsumer vertexConsumer3 = new SheetedDecalTextureGenerator(
                    bufferSource.getBuffer( ModelBakery.DESTROY_TYPES.get(submit.crumblingOverlay().progress())),
                    submit.crumblingOverlay().cameraPose(),
                    1.0F
            );
            model.render(((matrixStack1, polygon) -> {
                renderPolygon(
                        matrixStack1,
                        polygon,
                        submit.sprite() == null ? vertexConsumer3 : submit.sprite().wrap(vertexConsumer3),
                        submit
                );
            }), matrixStack);
        }

        matrixStack.popMatrix();
    }

    private <S extends BRState> void renderPolygon(MatrixStack matrixStack, Polygon polygon, VertexConsumer vertexConsumer, Submit<S> submit) {
        Vertex[] vertices = polygon.vertices();
        Vector3fc[] normals = polygon.normals();

        Matrix4f positionMat = matrixStack.position();
        Matrix3f normalMat = matrixStack.normal();

        for (int i = 0; i < 4; i++) {
            int index = Mth.clamp(i, 0, vertices.length - 1);
            Vertex vertex = vertices[index];
            Vector3fc normal = normals[index];

            normalMat.transform(new Vector3f(normal));

            Vector3f pos = new Vector3f(vertex.x(), vertex.y(), vertex.z());
            positionMat.transformPosition(vertex.x(), vertex.y(), vertex.z(), pos);

            vertexConsumer.addVertex(vertex.x(), vertex.y(), vertex.z(),
                    submit.color(),
                    vertex.u(), vertex.v(),
                    submit.overlay(), submit.light(),
                    normal.x(), normal.y(), normal.z());
        }
    }

    public record Submit<S extends BRState>(
            MatrixStack matrixStack,
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
                Vector3f vector3f = submit.matrixStack().position().transformPosition(new Vector3f());
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
