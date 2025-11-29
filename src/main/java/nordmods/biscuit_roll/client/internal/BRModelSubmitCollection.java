package nordmods.biscuit_roll.client.internal;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@ApiStatus.NonExtendable
public interface BRModelSubmitCollection extends BRModelSubmitStorage {
    default BRModelRenderer.Storage biscuit_roll$getSubmitStorage() {
        throw new AssertionError("Implemented in mixin");
    }
}
