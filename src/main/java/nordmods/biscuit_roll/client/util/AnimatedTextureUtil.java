package nordmods.biscuit_roll.client.util;

import net.minecraft.client.renderer.SpriteMapper;
import net.minecraft.client.resources.model.sprite.AtlasManager;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

import java.util.function.BiFunction;

/// Util class to help to deal with pain in the butt called animated textures.
///
/// If you wish to use animated textures anywhere in your project with models from this mod, you need to
/// 1) Register your own atlas via {@link net.fabricmc.fabric.api.client.rendering.v1.AtlasRegistry#register(AtlasManager.AtlasConfig)} or use existing one
/// 2) Override {@link nordmods.biscuit_roll.client.renderer.BRRenderer#getSpriteForTexture(Identifier)} in your renderer to get sprite from your atlas
///
/// Using this util class is not necessary to get them working
public class AnimatedTextureUtil {
    private static final BiFunction<SpriteMapper, Identifier, SpriteId> SPRITE_ID_FOR_MAPPER = Util.memoize((mapper, texture) -> {
        String path = texture.getPath();
        return mapper.apply(
                Identifier.fromNamespaceAndPath(
                        texture.getNamespace(),
                        path.substring(10 + mapper.prefix().length(), path.indexOf(".png"))
                )
        );
    });

    private static final BiFunction<Identifier, Identifier, SpriteId> SPRITE_ID_FOR_ATLAS = Util.memoize((atlasLocation, texture) -> {
        String path = texture.getPath();
        return new SpriteId(atlasLocation,
                Identifier.fromNamespaceAndPath(
                        texture.getNamespace(),
                        path.substring(9 + (atlasLocation.getPath().lastIndexOf(".png") - atlasLocation.getPath().lastIndexOf("/")), path.indexOf(".png"))
                )
        );
    });

    public static SpriteId getSpriteIdForMapper(SpriteMapper mapper, Identifier texture) {
        return SPRITE_ID_FOR_MAPPER.apply(mapper, texture);
    }

    public static SpriteId getSpriteIdForAtlas(Identifier atlasLocation, Identifier texture) {
        return SPRITE_ID_FOR_ATLAS.apply(atlasLocation, texture);
    }
}
