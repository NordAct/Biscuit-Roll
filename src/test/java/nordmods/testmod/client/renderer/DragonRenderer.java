package nordmods.testmod.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.client.renderer.BREntityRenderer;
import nordmods.biscuit_roll.client.state.ClientStateDataTypes;
import nordmods.testmod.client.model_provider.DroneModelProvider;
import nordmods.testmod.common.Dragon;
import nordmods.testmod.common.Drone;
import org.jetbrains.annotations.NotNull;

public class DragonRenderer extends BREntityRenderer<Dragon, LivingEntityRenderState> {
    public DragonRenderer(EntityRendererProvider.Context context) {
        super(context, new DroneModelProvider());
    }

    @Override
    public @NotNull LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    public RenderType getRenderType(LivingEntityRenderState state, Identifier texture) {
        return RenderTypes.entityCutoutNoCull(texture);
    }

    @Override
    public void extractRenderState(Dragon entity, LivingEntityRenderState state, float tickDelta) {
        super.extractRenderState(entity, state, tickDelta);

    }
}
