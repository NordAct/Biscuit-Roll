package nordmods.testmod.common;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import nordmods.biscuit_roll.common.animation.BRAnimatedObject;
import nordmods.biscuit_roll.common.animation.BRAnimationController;
import nordmods.biscuit_roll.common.animation.EntityAnimationController;

import java.util.Collection;
import java.util.List;

public class Drone extends Mob implements BRAnimatedObject {
    private final BRAnimationController controller1 = new EntityAnimationController<>(this, true);
    private final BRAnimationController controller2 = new EntityAnimationController<>(this, false);
    public Drone(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }
    private final String[] animations = {"idle", "attack"};

    private static final EntityDataAccessor<Integer> ANIMATION_ORDINAL = SynchedEntityData.defineId(Drone.class, EntityDataSerializers.INT);

    public void setAnimationOrdinal(int state) {
        entityData.set(ANIMATION_ORDINAL, state);
    }
    public int getAnimationOrdinal() {
        return entityData.get(ANIMATION_ORDINAL);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ANIMATION_ORDINAL, 0);
    }

    @Override
    public Collection<BRAnimationController> getAnimationControllers() {
        return List.of(controller1, controller2);
    }

    public void tick() {
        super.tick();
        if (level().isClientSide()) {
            controller2.playAnimation("default");
            controller1.playAnimation(animations[getAnimationOrdinal()]);
        }
    }

    protected InteractionResult mobInteract(Player player, InteractionHand interactionHand) {
        if (!level().isClientSide() && interactionHand == InteractionHand.MAIN_HAND) {
            if (!player.isShiftKeyDown())
                setAnimationOrdinal((getAnimationOrdinal() + 1) % animations.length);
        }
        return super.mobInteract(player, interactionHand);
    }
}
