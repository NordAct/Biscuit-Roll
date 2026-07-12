package nordmods.testmod.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.client.renderer.BRBlockEntityRenderer;
import nordmods.biscuit_roll.client.state.ClientStateDataTypes;
import nordmods.biscuit_roll.client.util.RenderUtil;
import nordmods.biscuit_roll.common.model.BRModelProvider;
import nordmods.biscuit_roll.common.state.BRState;
import nordmods.biscuit_roll.common.state.StateDataTypes;
import nordmods.testmod.TestMod;
import nordmods.testmod.common.block.DonutBlockEntity;
import org.joml.Vector3fc;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.function.Consumer;

// If you wish to use geometry models for items, do it so only if:
// 1. It's model of block with block entity
// 2. You need polymesh
// Also note that animating items separately is pain in the butt
public class DonutRenderer extends BRBlockEntityRenderer<DonutBlockEntity, BlockEntityRenderState> implements NoDataSpecialModelRenderer {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("minecraft", "textures/block/prismarine.png");
    private static final Identifier MODEL = Identifier.fromNamespaceAndPath(TestMod.MOD_ID, "biscuit_roll/models/donut.geo.json");
    private static final Identifier ANIMATION = Identifier.fromNamespaceAndPath(TestMod.MOD_ID, "biscuit_roll/animations/donut.animation.json");
    public DonutRenderer() {
        super(new BRModelProvider() {
            @Override
            public Identifier getModelId(BRState state) {
                return MODEL;
            }

            @Override
            public Identifier getAnimationId(BRState state) {
                return ANIMATION;
            }
        });
    }

    @Override
    public BlockEntityRenderState createRenderState() {
        return new BlockEntityRenderState();
    }

    @Override
    public RenderType getRenderType(BRState state, Identifier texture) {
        return RenderTypes.entityCutout(texture);
    }

    @Override
    public Identifier getTextureId(BRState state) {
        return TEXTURE;
    }

    @Override
    public void submit(@NonNull PoseStack poseStack, @NonNull SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        BlockEntityRenderState state = createRenderState();
        state.setStateData(StateDataTypes.CONTROLLERS, List.of());
        state.setStateData(StateDataTypes.MODEL_PROVIDER, getModelProvider());
        state.setStateData(ClientStateDataTypes.LIGHT, lightCoords);
        state.setStateData(ClientStateDataTypes.OVERLAY_TEXTURE, overlayCoords);
        state.setStateData(ClientStateDataTypes.OUTLINE_COLOR, outlineColor);
        submit(state, poseStack, submitNodeCollector, null);
    }

    @Override
    public void getExtents(@NonNull Consumer<Vector3fc> consumer) {
        PoseStack poseStack = new PoseStack();
        poseStack.scale(-1, -1, 1);
        BlockEntityRenderState state = createRenderState();
        state.setStateData(StateDataTypes.MODEL_PROVIDER, getModelProvider());
        RenderUtil.getExtentsForGui(getModel(state), poseStack, consumer);
    }

    @Override
    public int getCurrentTick(DonutBlockEntity blockEntity) {
        return blockEntity.ticks;
    }

    @Environment(EnvType.CLIENT)
    public record Unbaked() implements NoDataSpecialModelRenderer.Unbaked {
        public static final MapCodec<DonutRenderer.Unbaked> MAP_CODEC = MapCodec.unit(new DonutRenderer.Unbaked());

        @Override
        public @NonNull MapCodec<DonutRenderer.Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public @NonNull SpecialModelRenderer<Void> bake(@NonNull BakingContext bakingContext) {
            return new DonutRenderer();
        }
    }
}
