package nordmods.biscuit_roll.mixin;

import gg.moonflower.pinwheel.api.transform.MatrixStack;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.rendertype.RenderType;
import nordmods.biscuit_roll.client.internal.BRModelSubmitStorage;
import nordmods.biscuit_roll.common.model.BRModel;
import nordmods.biscuit_roll.common.state.BRState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SubmitNodeStorage.class)
public abstract class SubmitNodeStorageMixin implements BRModelSubmitStorage {
    @Shadow
    public abstract SubmitNodeCollection order(int i);

    @Override
    public <S extends BRState> void biscuit_roll$submit(MatrixStack matrixStack, BRModel<S> model, S state, RenderType renderType) {
        order(0).biscuit_roll$submit(matrixStack, model, state, renderType);
    }
}
