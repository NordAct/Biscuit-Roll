package nordmods.testmod.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import nordmods.biscuit_roll.common.animation.BRPlayingAnimation;
import nordmods.testmod.TestMod;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class DonutBlock extends BaseEntityBlock {
    public static final MapCodec<DonutBlock> CODEC = simpleCodec(DonutBlock::new);
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    public DonutBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(defaultBlockState().setValue(ACTIVE, false));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NonNull BlockPos blockPos, @NonNull BlockState blockState) {
        return new DonutBlockEntity(blockPos, blockState);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
    }

    @Override
    protected @NonNull MapCodec<? extends DonutBlock> codec() {
        return CODEC;
    }

    @Override
    protected @NonNull InteractionResult useWithoutItem(@NonNull BlockState blockState, @NonNull Level level, @NonNull BlockPos blockPos, @NonNull Player player, @NonNull BlockHitResult blockHitResult) {
        if (!level.isClientSide()) {
            blockState = blockState.cycle(ACTIVE);
            level.setBlock(blockPos, blockState, Block.UPDATE_ALL);
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NonNull Level level, @NonNull BlockState blockState, @NonNull BlockEntityType<T> blockEntityType) {
        return BaseEntityBlock.createTickerHelper(blockEntityType, TestMod.DONUT_BLOCK_ENTITY, DonutBlock::tickDonut);
    }

    public static void tickDonut(Level level, BlockPos blockPos, BlockState blockState, DonutBlockEntity blockEntity) {
        if (!level.isClientSide()) return;
        if (blockState.getValue(ACTIVE)) {
            blockEntity.controller.playAnimation("hover");
        } else {
            BRPlayingAnimation animation = blockEntity.controller.getAnimation("hover");
            if (animation != null) animation.stop();
        }
        blockEntity.ticks++;
    }
}
