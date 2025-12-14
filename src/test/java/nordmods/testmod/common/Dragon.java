package nordmods.testmod.common;

import gg.moonflower.pinwheel.api.animation.AnimationData;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import nordmods.biscuit_roll.common.animation.BRAnimatedObject;
import nordmods.biscuit_roll.common.animation.BRAnimationController;

import java.util.Collection;
import java.util.List;

public class Dragon extends Mob implements BRAnimatedObject {
    private final BRAnimationController controller = new BRAnimationController(this);
    public Dragon(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    private static final EntityDataAccessor<Boolean> RAINBOW = SynchedEntityData.defineId(Dragon.class, EntityDataSerializers.BOOLEAN);
    public void setIsBrown(boolean state) {
        entityData.set(RAINBOW, state);
    }
    public boolean isRainbow() {
        return entityData.get(RAINBOW);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(RAINBOW, false);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput valueOutput) {
        super.addAdditionalSaveData(valueOutput);
        valueOutput.putBoolean("Rainbow", isRainbow());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput valueInput) {
        super.readAdditionalSaveData(valueInput);
        setIsBrown(valueInput.getBooleanOr("Rainbow", false));
    }

    @Override
    public Collection<BRAnimationController> getAnimationControllers() {
        return List.of(controller);
    }

    @Override
    public boolean isClient() {
        return level().isClientSide();
    }

    @Override
    public void tick() {
        super.tick();
        if (isClient()) {
            controller.playAnimation("blink");
            controller.playAnimation(isRainbow() ? "dance" : "walk");
        }
    }

    protected InteractionResult mobInteract(Player player, InteractionHand interactionHand) {
        if (level().isClientSide()) {
            controller.playAnimation(
                    "attack.melee1",
                    new BRAnimationController.ProposedAnimationData(AnimationData.LerpMode.LINEAR, 0, AnimationData.LerpMode.LINEAR, 0)
            );
        }
        return super.mobInteract(player, interactionHand);
    }
}
