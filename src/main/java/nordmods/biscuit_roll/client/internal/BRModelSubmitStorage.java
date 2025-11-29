package nordmods.biscuit_roll.client.internal;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.rendertype.RenderType;
import nordmods.biscuit_roll.common.model.BRModel;
import nordmods.biscuit_roll.common.state.BRState;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@ApiStatus.NonExtendable
public interface BRModelSubmitStorage {
    default <S extends BRState> void biscuit_roll$submit(
            PoseStack.Pose pose,
            BRModel<S> model,
            S state,
            RenderType renderType
    ) {
        throw new AssertionError("Implemented in mixin");
    }
}
