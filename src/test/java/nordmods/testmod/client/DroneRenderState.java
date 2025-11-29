package nordmods.testmod.client;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import nordmods.biscuit_roll.state.BRState;
import nordmods.biscuit_roll.state.StateDataType;

import java.util.HashMap;
import java.util.Map;

public class DroneRenderState extends LivingEntityRenderState implements BRState { //todo move impl to mixin in lib
    private final Map<StateDataType<?>, StateDataType.Holder<?>> dataMap = new HashMap<>();

    @Override
    public Map<StateDataType<?>, StateDataType.Holder<?>> getDataMap() {
        return dataMap;
    }
}
