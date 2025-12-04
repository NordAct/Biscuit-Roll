package nordmods.testmod.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.client.renderer.BREntityRenderer;
import nordmods.biscuit_roll.client.state.ClientStateDataTypes;
import nordmods.testmod.client.TestModClient;
import nordmods.testmod.client.model_provider.DragonModelProvider;
import nordmods.testmod.common.Dragon;
import org.jetbrains.annotations.NotNull;

public class DragonRenderer extends BREntityRenderer<Dragon, LivingEntityRenderState> {
    public DragonRenderer(EntityRendererProvider.Context context) {
        super(context, new DragonModelProvider());
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
        state.setStateData(TestModClient.IS_DRAGON_BROWN, entity.isBrown());
    }
}
