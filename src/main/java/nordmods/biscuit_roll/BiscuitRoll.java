package nordmods.biscuit_roll;

import com.mojang.logging.LogUtils;
import gg.moonflower.molangcompiler.api.MolangCompiler;
import gg.moonflower.pinwheel.api.PinwheelMolangCompiler;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;

//todo: reminder to self - change license once done
public class BiscuitRoll implements ModInitializer {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "biscuit_roll";

    @Override
    public void onInitialize() {
        PinwheelMolangCompiler.set(MolangCompiler.create(MolangCompiler.DEFAULT_FLAGS, BiscuitRoll.class.getClassLoader())); //what
    }

    public static Identifier id(String id) {
        return Identifier.fromNamespaceAndPath(MOD_ID, id);
    }
}
