package nordmods.biscuit_roll.client.renderer;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import nordmods.biscuit_roll.model.BRModelProvider;
import nordmods.biscuit_roll.render_state.BRRenderState;
import org.jetbrains.annotations.NotNull;

public abstract class BREntityRenderer<E extends Entity, S extends EntityRenderState & BRRenderState> extends EntityRenderer<@NotNull E, @NotNull S> implements BRModelProvider<S> {
    protected final BRModelProvider<S> modelProvider;

    protected BREntityRenderer(EntityRendererProvider.Context context, BRModelProvider<S> modelProvider) {
        super(context);
        this.modelProvider = modelProvider;
    }


}
