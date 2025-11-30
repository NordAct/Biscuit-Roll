package nordmods.testmod;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import nordmods.testmod.common.Dragon;
import nordmods.testmod.common.Drone;
import org.slf4j.Logger;

public class TestMod implements ModInitializer {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "testmod";

    public static final EntityType<Drone> DRONE = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(MOD_ID, "drone"),
            EntityType.Builder
                    .of(Drone::new, MobCategory.MISC)
                    .sized(1, 1)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE,  Identifier.fromNamespaceAndPath(MOD_ID, "drone")))
    );
    public static final EntityType<Dragon> DRAGON = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(MOD_ID, "dragon"),
            EntityType.Builder
                    .of(Dragon::new, MobCategory.MISC)
                    .sized(2f, 2.9f)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE,  Identifier.fromNamespaceAndPath(MOD_ID, "dragon")))
    );
    @Override
    public void onInitialize() {
        LOGGER.info("Hello from Biscuit Roll Test Mod");
        FabricDefaultAttributeRegistry.register(DRONE, Mob.createMobAttributes());
        FabricDefaultAttributeRegistry.register(DRAGON, Mob.createMobAttributes());
    }
}
