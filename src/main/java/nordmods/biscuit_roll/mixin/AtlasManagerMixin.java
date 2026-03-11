package nordmods.biscuit_roll.mixin;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.sprite.AtlasManager;
import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.client.util.AnimatedTextureUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(AtlasManager.class)
public class AtlasManagerMixin {
    @Shadow
    @Final
    private Map<Identifier, AtlasManager.AtlasEntry> atlasByTexture;

    @Shadow
    @Final
    private Map<Identifier, AtlasManager.AtlasEntry> atlasById;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void aVeryBadIdea(TextureManager textureManager, int i, CallbackInfo ci) {
        AtlasManager.AtlasConfig config = new AtlasManager.AtlasConfig(
                AnimatedTextureUtil.ANIMATED_TEXTURES_TEXTURE_ID,
                AnimatedTextureUtil.ANIMATED_TEXTURES_ATLAS_ID,
                false
        );
        TextureAtlas textureAtlas = new TextureAtlas(config.textureId());
        textureManager.register(config.textureId(), textureAtlas);
        AtlasManager.AtlasEntry atlasEntry = new AtlasManager.AtlasEntry(textureAtlas, config);
        atlasByTexture.put(config.textureId(), atlasEntry);
        atlasById.put(config.definitionLocation(), atlasEntry);
    }
}
