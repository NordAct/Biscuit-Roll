package nordmods.biscuit_roll.client.internal;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBakery;
import nordmods.biscuit_roll.client.state.ClientStateDataTypes;
import nordmods.biscuit_roll.common.model.BRModel;
import nordmods.biscuit_roll.common.state.BRState;
import org.jetbrains.annotations.ApiStatus;
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
            MultiBufferSource.BufferSource crumblingBufferSource
    ) {
        Storage storage = submitNodeCollection.biscuit_roll$getSubmitStorage();
        renderOpaque(bufferSource, outlineBufferSource, storage.opaque, crumblingBufferSource);
        storage.translucent.sort(Comparator.comparingDouble(translucentModelSubmit -> -translucentModelSubmit.position().lengthSquared()));
        renderTranslucent(bufferSource, outlineBufferSource, storage.translucent, crumblingBufferSource);
    }

    private void renderTranslucent(
            MultiBufferSource.BufferSource bufferSource,
            OutlineBufferSource outlineBufferSource,
            List<TranslucentSubmit> list,
            MultiBufferSource.BufferSource crumblingBufferSource
    ) {
        for (TranslucentSubmit translucentModelSubmit : list) {
            this.renderModel(
                    translucentModelSubmit.submit(),
                    translucentModelSubmit.renderType(),
                    bufferSource.getBuffer(translucentModelSubmit.renderType()),
                    outlineBufferSource,
                    crumblingBufferSource
            );
        }
    }

    private void renderOpaque(
            MultiBufferSource.BufferSource bufferSource,
            OutlineBufferSource outlineBufferSource,
            Map<RenderType, List<Submit>> map,
            MultiBufferSource.BufferSource bufferSource2
    ) {
        for (Map.Entry<RenderType, List<Submit>> entry : map.entrySet()) {
            VertexConsumer vertexConsumer = bufferSource.getBuffer(entry.getKey());

            for (Submit submit : entry.getValue()) {
                this.renderModel(submit, entry.getKey(), vertexConsumer, outlineBufferSource, bufferSource2);
            }
        }
    }

    private void renderModel(
            Submit submit,
            RenderType renderType,
            VertexConsumer textureBuffer,
            OutlineBufferSource outlineBufferSource,
            MultiBufferSource.BufferSource bufferSource
    ) {
        stack.pushPose();
        stack.last().set(submit.pose());

        submit.model.applyAnimations(submit.state);
        submit.model.render(stack, submit.state, submit.sprite == null ? textureBuffer : submit.sprite.wrap(textureBuffer));

        int outline = submit.state.getStateDataOptional(ClientStateDataTypes.OUTLINE_COLOR).orElse(0);
        if (outline != 0 && (renderType.outline().isPresent() || renderType.isOutline())) {
            outlineBufferSource.setColor(outline);
            VertexConsumer outlineBuffer = outlineBufferSource.getBuffer(renderType);
            submit.model.render(stack, submit.state, submit.sprite == null ? outlineBuffer : submit.sprite.wrap(outlineBuffer));
        }

        ModelFeatureRenderer.CrumblingOverlay overlay = submit.state.getStateData(ClientStateDataTypes.CRUMBLING_OVERLAY);
        if (overlay != null && renderType.affectsCrumbling()) {
            VertexConsumer overlayBuffer = new SheetedDecalTextureGenerator(
                    bufferSource.getBuffer( ModelBakery.DESTROY_TYPES.get(overlay.progress())),
                    overlay.cameraPose(),
                    1.0F
            );
            submit.model.render(stack, submit.state, submit.sprite == null ? overlayBuffer : submit.sprite.wrap(overlayBuffer));
        }

        stack.popPose();
    }

    public record Submit(
            PoseStack.Pose pose,
            BRModel model,
            BRState state,
            @Nullable TextureAtlasSprite sprite
    ) {}

    public record TranslucentSubmit(
       Submit submit,
       RenderType renderType,
       Vector3f position
    ) {}

    public static class Storage {
        private final Map<RenderType, List<Submit>> opaque = new HashMap<>();
        private final List<TranslucentSubmit> translucent = new ArrayList<>();
        private final Set<RenderType> used = new ObjectOpenHashSet<>();

        public void add(RenderType renderType, Submit submit) {
            if (renderType.pipeline().getBlendFunction().isEmpty()) {
                opaque.computeIfAbsent(renderType, renderTypex -> new ArrayList<>()).add(submit);
            } else {
                Vector3f vector3f = submit.pose().pose().transformPosition(new Vector3f());
                translucent.add(new TranslucentSubmit(submit, renderType, vector3f));
            }
        }

        public void clear() {
            this.translucent.clear();
            for (Map.Entry<RenderType, List<Submit>> entry : opaque.entrySet()) {
                List<Submit> list = entry.getValue();
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
