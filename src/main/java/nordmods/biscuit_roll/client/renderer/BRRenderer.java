package nordmods.biscuit_roll.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.client.internal.BRModelSubmitStorage;
import nordmods.biscuit_roll.client.renderer.layer.BRRenderLayer;
import nordmods.biscuit_roll.client.util.ClientModelManager;
import nordmods.biscuit_roll.common.animation.BRAnimationController;
import nordmods.biscuit_roll.common.model.BRModel;
import nordmods.biscuit_roll.common.model.BRModelProvider;
import nordmods.biscuit_roll.common.state.BRState;
import nordmods.biscuit_roll.common.state.StateDataTypes;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@SuppressWarnings("unused")
public interface BRRenderer<S extends BRState> {
    @Nullable
    default BRModel getModel(S state) {
        return ClientModelManager.instance().getModel(getModelProvider().getModelId(state));
    }

    BRModelProvider getModelProvider();
    RenderType getRenderType(BRState state, Identifier texture);
    Identifier getTextureId(BRState state);

    default void beforeSubmit(S state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {}

    default void afterSubmit(S state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {}

    default void submitBRModel(S state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        submit(state, poseStack, submitNodeCollector, cameraRenderState, (BRModelSubmitStorage) submitNodeCollector);
    }

    default void submitBRModelOrdered(S state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState, int order) {
        submit(state, poseStack, submitNodeCollector, cameraRenderState, (BRModelSubmitStorage) submitNodeCollector.order(order));
    }

    @ApiStatus.Internal
    @ApiStatus.NonExtendable
    default void submit(S state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState, BRModelSubmitStorage brModelSubmitStorage) {
        poseStack.pushPose();

        beforeSubmit(state, poseStack, submitNodeCollector, cameraRenderState);

        Identifier texture = getTextureId(state);
        BRModel model = getModel(state);
        submitModel(poseStack, model, state, this::getRenderType, texture, brModelSubmitStorage);

        Collection<BRAnimationController> controllers = state.getStateData(StateDataTypes.CONTROLLERS);
        controllers.forEach(controller -> controller.update(state));
        model.applyAnimations(state); //vanilla does this as well because there's no other way to obtain accurate bone/locator transformations in render layers

        for (BRRenderLayer renderLayer : getRenderLayers()) {
            if (renderLayer.canRender(state)) renderLayer.submitLayer(BRState.copy(state), poseStack, submitNodeCollector, cameraRenderState);
        }
        afterSubmit(state, poseStack, submitNodeCollector, cameraRenderState);

        poseStack.popPose();
    }

    Collection<BRRenderLayer> getRenderLayers();

    default void addRenderLayer(BRRenderLayer renderLayer) {
        getRenderLayers().add(renderLayer);
    }

    static void submitModel(
            PoseStack poseStack,
            BRModel model,
            BRState state,
            BRModelSubmitStorage.RenderTypeProvider renderTypeProvider,
            Identifier texture,
            BRModelSubmitStorage submitNodeCollector
    ) {
        if (model == null) {
            throw new IllegalStateException("Attempted to submit null model. Please check if model you trying to submit is loaded");
        }
        submitNodeCollector.biscuit_roll$submit(
                poseStack.last().copy(),
                model,
                state,
                renderTypeProvider,
                texture
        );
    }
}
