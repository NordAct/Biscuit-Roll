package nordmods.biscuit_roll.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.BiscuitRoll;

public class AnimatedTextureUtil {
    private static final String ANIMATED_TEXTURES = "animated_textures";
    private static final String PATH_START = "textures/" + ANIMATED_TEXTURES + "/";
    public static final Identifier ANIMATED_TEXTURES_TEXTURE_ID = BiscuitRoll.id("textures/" + ANIMATED_TEXTURES + ".png");
    public static final Identifier ANIMATED_TEXTURES_ATLAS_ID = BiscuitRoll.id(ANIMATED_TEXTURES);

    public static TextureAtlasSprite getAnimatedTextureSprite(Identifier texture) {
        String path = texture.getPath();
        if (!path.contains(PATH_START)) return null;
        Identifier animatedTextureId = Identifier.fromNamespaceAndPath(texture.getNamespace(), path.substring(PATH_START.length(), path.indexOf(".png")));
        return Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(ANIMATED_TEXTURES_ATLAS_ID).getSprite(animatedTextureId);
    }
}
