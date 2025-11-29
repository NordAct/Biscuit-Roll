package nordmods.biscuit_roll.mixin;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import nordmods.biscuit_roll.common.state.BRState;
import nordmods.biscuit_roll.common.state.StateDataType;
import org.spongepowered.asm.mixin.Mixin;

import java.util.HashMap;
import java.util.Map;

@Mixin(EntityRenderState.class)
public abstract class RenderStateMixin implements BRState {
    private final Map<StateDataType<?>, StateDataType.Holder<?>> dataMap = new HashMap<>();

    @Override
    public Map<StateDataType<?>, StateDataType.Holder<?>> getDataMap() {
        return dataMap;
    }
}
