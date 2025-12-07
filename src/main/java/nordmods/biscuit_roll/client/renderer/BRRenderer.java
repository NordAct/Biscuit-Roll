package nordmods.biscuit_roll.client.renderer;

import nordmods.biscuit_roll.client.util.ClientModelManager;
import nordmods.biscuit_roll.common.model.BRModel;
import nordmods.biscuit_roll.common.model.BRModelProvider;
import nordmods.biscuit_roll.common.state.BRState;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public interface BRRenderer<S extends BRState> {
    @Nullable
    default BRModel getModel(S state) {
        return ClientModelManager.instance().getModel(getModelProvider().getModelId(state));
    }

    BRModelProvider getModelProvider();
}
