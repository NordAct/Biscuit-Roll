package nordmods.testmod.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.client.renderer.BREntityRenderer;
import nordmods.testmod.common.Drone;
import org.jetbrains.annotations.NotNull;

public class DroneRenderer extends BREntityRenderer<Drone, DroneRenderState> {
    public DroneRenderer(EntityRendererProvider.Context context) {
        super(context, new DroneModelProvider());
    }

    @Override
    public @NotNull DroneRenderState createRenderState() {
        return new DroneRenderState();
    }

    @Override
    public RenderType getRenderType(DroneRenderState state, Identifier texture) {
        return RenderTypes.entityCutoutNoCull(texture);
    }
}
