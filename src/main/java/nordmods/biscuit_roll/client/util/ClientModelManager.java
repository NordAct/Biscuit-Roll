package nordmods.biscuit_roll.client.util;

import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.common.model.BRModel;
import nordmods.biscuit_roll.common.state.BRState;
import nordmods.biscuit_roll.common.util.BRModelManager;

import java.util.HashMap;
import java.util.Map;

public class ClientModelManager<S extends BRState> extends BRModelManager<S> {
    private static final ClientModelManager<?> INSTANCE = new ClientModelManager();
    private final Map<Identifier, BRModel<S>> modelRegistry = new HashMap<>();

    private ClientModelManager() {

    }

    @Override
    protected Map<Identifier, BRModel<S>> getHolderMap() {
        return modelRegistry;
    }

    public static ClientModelManager<?> instance() {
        return INSTANCE;
    }
}
