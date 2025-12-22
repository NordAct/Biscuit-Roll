package nordmods.biscuit_roll.client.internal;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface BRModelSubmitCollection extends BRModelSubmitStorage {
    @ApiStatus.Internal
    default BRModelRenderer.Storage biscuit_roll$getSubmitStorage() {
        throw new AssertionError("Implemented in mixin");
    }
}
