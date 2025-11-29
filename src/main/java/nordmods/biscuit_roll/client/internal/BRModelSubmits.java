package nordmods.biscuit_roll.client.internal;

import gg.moonflower.pinwheel.api.transform.MatrixStack;
import net.minecraft.client.renderer.rendertype.RenderType;
import nordmods.biscuit_roll.model.BRModel;
import nordmods.biscuit_roll.state.BRState;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@ApiStatus.NonExtendable
public interface BRModelSubmits {
    BRModelRenderer.Storage biscuit_roll$getSubmitStorage();
    <S extends BRState> void biscuit_roll$submit(
            MatrixStack matrixStack,
            BRModel<S> model,
            S state,
            RenderType renderType
    );
}
