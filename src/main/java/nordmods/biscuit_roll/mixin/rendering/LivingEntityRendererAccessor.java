package nordmods.biscuit_roll.mixin.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntityRenderer.class)
public interface LivingEntityRendererAccessor<S extends LivingEntityRenderState> {
    @Invoker
    float invokeGetWhiteOverlayProgress(final S state);

    @Invoker
    void invokeSetupRotations(final S state, final PoseStack poseStack, float bodyRot, final float entityScale);
}
