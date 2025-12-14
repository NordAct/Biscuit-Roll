package nordmods.biscuit_roll.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.client.internal.BRModelRenderer;
import nordmods.biscuit_roll.client.internal.BRModelSubmitCollection;
import nordmods.biscuit_roll.client.util.AnimatedTextureUtil;
import nordmods.biscuit_roll.common.model.BRModel;
import nordmods.biscuit_roll.common.state.BRState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SubmitNodeCollection.class)
public class SubmitNodeCollectionMixin implements BRModelSubmitCollection {
    @Shadow
    private boolean wasUsed;
    @Unique
    private final BRModelRenderer.Storage biscuit_roll$storage = new BRModelRenderer.Storage();
    @Override
    public BRModelRenderer.Storage biscuit_roll$getSubmitStorage() {
        return biscuit_roll$storage;
    }

    @Override
    public void biscuit_roll$submit(PoseStack.Pose pose, BRModel model, BRState state, RenderTypeProvider renderTypeProvider, Identifier texture) {
        wasUsed = true;
        TextureAtlasSprite sprite = AnimatedTextureUtil.getAnimatedTextureSprite(texture);
        biscuit_roll$storage.add(renderTypeProvider.getRenderType(state, sprite == null ? texture : sprite.atlasLocation()),
                new BRModelRenderer.Submit(
                        pose,
                        model,
                        state,
                        sprite
                ));
    }

    @Inject(method = "clear", at = @At("TAIL"))
    private void clearStorage(CallbackInfo ci) {
        biscuit_roll$storage.clear();
    }

    @Inject(method = "endFrame", at = @At("HEAD"))
    private void onEndFrame(CallbackInfo ci) {
        biscuit_roll$storage.endFrame();
    }
}
