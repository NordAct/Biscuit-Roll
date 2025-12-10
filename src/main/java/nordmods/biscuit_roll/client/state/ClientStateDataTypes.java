package nordmods.biscuit_roll.client.state;

import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import nordmods.biscuit_roll.BiscuitRoll;
import nordmods.biscuit_roll.common.state.StateDataType;

public class ClientStateDataTypes {
    public static final StateDataType<ModelFeatureRenderer.CrumblingOverlay> CRUMBLING_OVERLAY = new StateDataType<>(BiscuitRoll.id("crumbling_overlay"));
    public static final StateDataType<Integer> OVERLAY_TEXTURE = new StateDataType<>(BiscuitRoll.id("overlay_texture"));
    public static final StateDataType<Integer> OUTLINE_COLOR = new StateDataType<>(BiscuitRoll.id("outline_color"));
    public static final StateDataType<Integer> COLOR = new StateDataType<>(BiscuitRoll.id("color"));
    public static final StateDataType<Integer> LIGHT = new StateDataType<>(BiscuitRoll.id("light"));
}
