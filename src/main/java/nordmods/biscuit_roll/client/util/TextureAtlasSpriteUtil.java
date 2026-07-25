package nordmods.biscuit_roll.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SpriteMapper;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.AtlasManager;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

import java.util.function.BiFunction;

/// Util class to help to deal with pain in the butt called texture atlas sprites.
/// Using this util class is not necessary to get them working. It exists to help with the fact of library using absolute texture paths instead of ids
///
/// If you wish to use texture atlas sprites anywhere in your project with models from this mod, you need to
/// 1) Register your own atlas via {@link net.fabricmc.fabric.api.client.rendering.v1.AtlasRegistry#register(AtlasManager.AtlasConfig)} or use existing one
/// 2) Override {@link nordmods.biscuit_roll.client.renderer.BRRenderer#getSpriteForTexture(Identifier)} in your renderer to get sprite from your atlas
///
public class TextureAtlasSpriteUtil {
    private static final Identifier MISSING_TEXTURE = MissingTextureAtlasSprite.getLocation();
    private static final BiFunction<Identifier, Identifier, SpriteId> SPRITE_ID_FROM_SHEET = Util.memoize(SpriteId::new);
    private static final BiFunction<SpriteMapper, Identifier, SpriteId> SPRITE_ID_FROM_SPRITE_MAKER = Util.memoize((SpriteMapper::apply));
    private static final BiFunction<Identifier, String, Identifier> RELATIVE_TEXTURE = Util.memoize(
            (texture, prefix) -> relativizeTexture(texture, prefix.isEmpty() ? "" : prefix + "/")
    );

    @Nullable
    public static TextureAtlasSprite getTextureAtlasSprite(SpriteId spriteId) {
        TextureAtlasSprite result = Minecraft.getInstance().getAtlasManager().get(spriteId);
        return result.contents().name().equals(MISSING_TEXTURE) ? null : result;
    }

    @Nullable
    public static TextureAtlasSprite getTextureAtlasSprite(Identifier sheet, Identifier texture, String texturePrefix) {
        return getTextureAtlasSprite(SPRITE_ID_FROM_SHEET.apply(sheet, RELATIVE_TEXTURE.apply(texture, texturePrefix)));
    }

    @Nullable
    public static TextureAtlasSprite getTextureAtlasSprite(SpriteMapper mapper, Identifier texture) {
        return getTextureAtlasSprite(mapper, texture, mapper.prefix());
    }

    @Nullable
    public static TextureAtlasSprite getTextureAtlasSprite(SpriteMapper mapper, Identifier texture, String texturePrefix) {
        return getTextureAtlasSprite(SPRITE_ID_FROM_SPRITE_MAKER.apply(mapper, RELATIVE_TEXTURE.apply(texture, texturePrefix)));
    }

    private static Identifier relativizeTexture(Identifier texture, String texturePrefix) {
        String path = texture.getPath();
        int pngIndex = path.indexOf(".png");
        if (pngIndex > 0) path = path.substring(0, pngIndex);
        if (path.startsWith("textures/")) path = path.substring(9); //9 - "textures/" length
        if (path.startsWith(texturePrefix)) path = path.substring(texturePrefix.length());
        return texture.withPath(path);
    }
}
