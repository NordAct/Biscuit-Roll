package nordmods.biscuit_roll.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.client.internal.BRModelSubmitStorage;
import nordmods.biscuit_roll.common.model.BRModel;
import nordmods.biscuit_roll.common.state.BRState;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SubmitNodeStorage.class)
public abstract class SubmitNodeStorageMixin implements BRModelSubmitStorage {
    @Shadow
    public abstract SubmitNodeCollection order(int i);

    @Override
    public void biscuit_roll$submit(PoseStack.Pose pose, BRModel model, BRState state, RenderTypeProvider renderTypeProvider, Identifier texture, @Nullable TextureAtlasSprite sprite) {
        order(0).biscuit_roll$submit(pose, model, state, renderTypeProvider, texture, sprite);
    }
}
