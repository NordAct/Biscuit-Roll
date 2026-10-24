package nordmods.biscuit_roll.common.util;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import nordmods.biscuit_roll.BiscuitRoll;
import nordmods.biscuit_roll.common.animation.processor.ControllerState;

import java.util.ArrayList;
import java.util.List;

public class BRAttachments {
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
}
