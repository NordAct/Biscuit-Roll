package nordmods.testmod;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import nordmods.testmod.common.block.DonutBlock;
import nordmods.testmod.common.block.DonutBlockEntity;
import nordmods.testmod.common.entity.Dragon;
import nordmods.testmod.common.entity.Drone;
import nordmods.testmod.common.entity.MeshtestEntity;
import nordmods.testmod.common.entity.WaterDragon;
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
    public static final EntityType<WaterDragon> WATER_DRAGON = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(MOD_ID, "water_dragon"),
            EntityType.Builder
                    .of(WaterDragon::new, MobCategory.MISC)
                    .sized(2.9f, 1.5f)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE,  Identifier.fromNamespaceAndPath(MOD_ID, "water_dragon")))
    );

    public static final EntityType<MeshtestEntity> MESHTEST = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(MOD_ID, "meshtest"),
            EntityType.Builder
                    .of(MeshtestEntity::new, MobCategory.MISC)
                    .sized(2.9f, 2.9f)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE,  Identifier.fromNamespaceAndPath(MOD_ID, "meshtest")))
    );

    public static final Block DONUT_BLOCK = Blocks.register(
            ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(MOD_ID, "donut")),
            DonutBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.TERRACOTTA_LIGHT_GRAY)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .strength(1.5F, 3.0F)
                    .sound(SoundType.MUD_BRICKS)
                    .noCollision()
    );
    public static final Item DONUT = Registry.register(
            BuiltInRegistries.ITEM,
            Identifier.fromNamespaceAndPath(MOD_ID, "donut"),
            new BlockItem(
                    DONUT_BLOCK,
                    new Item.Properties().setId(ResourceKey.create(Registries.ITEM,
                            Identifier.fromNamespaceAndPath(MOD_ID, "donut"))).useBlockDescriptionPrefix()
            )
    );
    public static final BlockEntityType<DonutBlockEntity> DONUT_BLOCK_ENTITY = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(MOD_ID, "donut"),
            FabricBlockEntityTypeBuilder.create(DonutBlockEntity::new, DONUT_BLOCK).build()
    );
    @Override
    public void onInitialize() {
        LOGGER.info("Hello from Biscuit Roll Test Mod");
        FabricDefaultAttributeRegistry.register(DRONE, Mob.createMobAttributes());
        FabricDefaultAttributeRegistry.register(DRAGON, Mob.createMobAttributes());
        FabricDefaultAttributeRegistry.register(WATER_DRAGON, Mob.createMobAttributes());
    }
}
