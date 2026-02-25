package nordmods.biscuit_roll.mixin;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import nordmods.biscuit_roll.client.internal.InjectedBRState;
import nordmods.biscuit_roll.common.state.StateDataType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashMap;
import java.util.Map;

@Mixin({
        EntityRenderState.class,
        BlockEntityRenderState.class
})
public abstract class RenderStateMixin implements InjectedBRState {
    @Unique
    private final Map<StateDataType<?>, StateDataType.Holder<?>> biscuit_roll$dataMap = new HashMap<>();

    @Override
    public Map<StateDataType<?>, StateDataType.Holder<?>> biscuit_roll$getInjectedDataMap() {
        return biscuit_roll$dataMap;
    }
}
