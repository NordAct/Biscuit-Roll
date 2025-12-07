package nordmods.biscuit_roll.client.internal;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.common.model.BRModel;
import nordmods.biscuit_roll.common.state.BRState;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@ApiStatus.NonExtendable
public interface BRModelSubmitStorage {
    default void biscuit_roll$submit(
            PoseStack.Pose pose,
            BRModel model,
            BRState state,
            RenderTypeProvider renderTypeProvider,
            Identifier texture
            ) {
        throw new AssertionError("Implemented in mixin");
    }

    interface RenderTypeProvider {
        RenderType getRenderType(BRState state, Identifier texture);
    }
}
