package nordmods.biscuit_roll.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.BiscuitRoll;

/// Util class to help to deal with pain in the butt called animated textures.
///
/// If you wish to use animated textures anywhere in your project with models from this mod, you should place them in `animated_textures` folder in `textures` folder, i.e.:
/// `your_name_space:textures/animated_textures/your_texture.png`. To animate the texture, do same steps as for animating vanilla textures
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
