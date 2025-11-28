package nordmods.testmod;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;

public class TestMod implements ModInitializer {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "testmod";
    @Override
    public void onInitialize() {
        LOGGER.info("Hello from Biscuit Roll Test Mod");
    }
}
