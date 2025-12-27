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
import nordmods.biscuit_roll.common.animation.EntityAnimationController;

import java.util.Collection;
import java.util.List;

public class Dragon extends Mob implements BRAnimatedObject {
    private final BRAnimationController controller0 = new EntityAnimationController<>(this, false);
    private final BRAnimationController controller1 = new EntityAnimationController<>(this, true);
    private String[] animations = {"idle", "walk", "dance", "fly.idle", "fly.straight"};
    public Dragon(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    private static final EntityDataAccessor<Boolean> RAINBOW = SynchedEntityData.defineId(Dragon.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> ANIMATION_ORDINAL = SynchedEntityData.defineId(Dragon.class, EntityDataSerializers.INT);
    public void setRainbow(boolean state) {
        entityData.set(RAINBOW, state);
    }
    public boolean isRainbow() {
        return entityData.get(RAINBOW);
    }

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
        builder.define(RAINBOW, false);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput valueOutput) {
        super.addAdditionalSaveData(valueOutput);
        valueOutput.putBoolean("Rainbow", isRainbow());
        valueOutput.putInt("Animation", getAnimationOrdinal());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput valueInput) {
        super.readAdditionalSaveData(valueInput);
        setRainbow(valueInput.getBooleanOr("Rainbow", false));
        setAnimationOrdinal(valueInput.getIntOr("Animation", 0));
    }

    @Override
    public Collection<BRAnimationController> getAnimationControllers() {
        return List.of(controller1, controller0);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide()) {
            controller0.playAnimation("blink");
            controller1.playAnimation(animations[getAnimationOrdinal()]);
            if (isRainbow()) controller1.getPlayingAnimations().forEach(animation -> animation.setSpeed((float) (Math.sin(tickCount / 20f / 2f) * 3 + 3)));
        }
    }

    protected InteractionResult mobInteract(Player player, InteractionHand interactionHand) {
        if (level().isClientSide()) {
            if (player.isShiftKeyDown()) {
                controller0.playAnimation(
                        "attack.melee1",
                        0, 0,
                        AnimationData.LerpMode.LINEAR, AnimationData.LerpMode.LINEAR
                );
            }
        } else {
            if (!player.isShiftKeyDown()) setAnimationOrdinal((getAnimationOrdinal() + 1) % animations.length);
        }
        return super.mobInteract(player, interactionHand);
    }
}
