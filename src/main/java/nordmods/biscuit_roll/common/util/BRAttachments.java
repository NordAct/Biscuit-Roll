package nordmods.biscuit_roll.common.util;

import nordmods.biscuit_roll.BiscuitRoll;
import nordmods.biscuit_roll.common.animation.processor.ControllerState;
//? if =fabric {
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
//?}
//? if =neoforge {
/*import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.AttachmentSyncHandler;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.bus.api.IEventBus;
import nordmods.biscuit_roll.common.animation.BRAnimationProcessorOwner;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;
*///?}

import java.util.ArrayList;
import java.util.List;


public class BRAttachments {
    //? if =fabric {
    public static final AttachmentType<List<ControllerState>> CONTROLLER_STATES = AttachmentRegistry.create(
            BiscuitRoll.id("controller_states"),
            builder -> builder
                    .initializer(ArrayList::new)
                    .syncWith(
                            ControllerState.LIST_STREAM_CODEC,
                            AttachmentSyncPredicate.all()
                    )
    );

    public static void init() {}
    //?}
    //? if =neoforge {
    /*private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, BiscuitRoll.MOD_ID);
    public static final Supplier<AttachmentType<List<ControllerState>>> CONTROLLER_STATES = ATTACHMENT_TYPES.register(
            "controller_states",
            () -> AttachmentType
                    .builder(() -> (List<ControllerState>)(Object)new ArrayList<>()) //¯\_(ツ)_/¯
                    .sync(new ControllerStateSyncHandler())
                    .build()
    );

    public static void init(IEventBus modBus) {
        ATTACHMENT_TYPES.register(modBus);
    }

    private static final class ControllerStateSyncHandler implements AttachmentSyncHandler<List<ControllerState>> {

        @Override
        public void write(RegistryFriendlyByteBuf buf, List<ControllerState> attachment, boolean initialSync) {
            ControllerState.LIST_STREAM_CODEC.encode(buf, attachment);
        }

        @Override
        public List<ControllerState> read(IAttachmentHolder holder, RegistryFriendlyByteBuf buf, @Nullable List<ControllerState> previousValue) {
            List<ControllerState> newValue = ControllerState.LIST_STREAM_CODEC.decode(buf);
            if (holder instanceof BRAnimationProcessorOwner<?> animationProcessorOwner) {
                ControllerState.applyControllerStates(newValue, animationProcessorOwner.getAnimationControllers());
                animationProcessorOwner.onAnimationControllersUpdated();
            }
            return newValue;
        }
    }
    *///?}
}
