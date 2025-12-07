package nordmods.testmod.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.client.renderer.BREntityRenderer;
import nordmods.biscuit_roll.common.state.BRState;
import nordmods.testmod.client.model_provider.DroneModelProvider;
import nordmods.testmod.common.Drone;
import org.jetbrains.annotations.NotNull;

public class DroneRenderer extends BREntityRenderer<Drone, LivingEntityRenderState> {
    public DroneRenderer(EntityRendererProvider.Context context) {
        super(context, new DroneModelProvider());
    }

    @Override
    public @NotNull LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    public RenderType getRenderType(BRState state, Identifier texture) {
        return RenderTypes.entityCutoutNoCull(texture);
    }
}
