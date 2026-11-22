package nordmods.biscuit_roll;

import com.mojang.logging.LogUtils;
import gg.moonflower.molangcompiler.api.MolangCompiler;
import gg.moonflower.pinwheel.api.PinwheelMolangCompiler;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

/// Initializer class for both sides
//reminder to self - change license once done
public class BiscuitRoll{
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "biscuit_roll";

    public static void commonInit() {
        PinwheelMolangCompiler.set(MolangCompiler.create(MolangCompiler.DEFAULT_FLAGS, BiscuitRoll.class.getClassLoader())); //what
    }

    @ApiStatus.Internal
    public static Identifier id(String id) {
        return Identifier.fromNamespaceAndPath(MOD_ID, id);
    }
}
