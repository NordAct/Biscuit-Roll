package nordmods.testmod.common.entity;

import com.mojang.math.Axis;
import gg.moonflower.molangcompiler.api.exception.MolangRuntimeException;
import gg.moonflower.pinwheel.api.animation.AnimationData;
import gg.moonflower.pinwheel.api.transform.LocatorTransformation;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import nordmods.biscuit_roll.common.animation.BRAnimatedObject;
import nordmods.biscuit_roll.common.animation.controller.BRAnimationController;
import nordmods.biscuit_roll.common.model.BRModel;
import nordmods.biscuit_roll.common.state.BRState;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.List;

public class Dragon extends Mob implements BRAnimatedObject {
    private final BRAnimationController controller0 = new DragonAnimationController(false);
    private final BRAnimationController controller1 = new DragonAnimationController(true);
    private final String[] animations = {"idle", "walk", "dance", "fly.idle", "fly.straight"};
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
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ANIMATION_ORDINAL, 0);
        builder.define(RAINBOW, false);
    }

    @Override
    protected void addAdditionalSaveData(@NonNull ValueOutput valueOutput) {
        super.addAdditionalSaveData(valueOutput);
        valueOutput.putBoolean("Rainbow", isRainbow());
        valueOutput.putInt("Animation", getAnimationOrdinal());
    }

    @Override
    protected void readAdditionalSaveData(@NonNull ValueInput valueInput) {
        super.readAdditionalSaveData(valueInput);
        setRainbow(valueInput.getBooleanOr("Rainbow", false));
        setAnimationOrdinal(valueInput.getIntOr("Animation", 0));
    }

    @Override
    public List<BRAnimationController> getAnimationControllers() {
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

    @Override
    protected @NonNull InteractionResult mobInteract(@NonNull Player player, @NonNull InteractionHand interactionHand) {
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

    // you might want to move this to separate class if you wish to use client-only stuff to avoid any issues with dedicated server during class loading
    private class DragonAnimationController extends BRAnimationController {
        public DragonAnimationController(boolean singleAnimation) {
            super(singleAnimation);
        }

        @Override
        protected void onSoundEffect(AnimationData.SoundEffect soundEffect, BRModel model, BRState state) {
            try {
                if (level().isClientSide()) {
                    playSound(
                            SoundEvent.createVariableRangeEvent(Identifier.tryParse(soundEffect.effect())),
                            DragonAnimationController.this.getEnvironment().resolve(soundEffect.pitch()),
                            DragonAnimationController.this.getEnvironment().resolve(soundEffect.volume())
                    );
                }
            } catch (MolangRuntimeException molangRuntimeException) {
                throw (new RuntimeException("Failed to play sound effect " + soundEffect.effect() + " at " + soundEffect.time(), molangRuntimeException));
            }
        }

        @Override
        protected void onParticleEffect(AnimationData.ParticleEffect particleEffect, BRModel model, BRState state) {
            if (level().isClientSide()) {
                LocatorTransformation transformation = model.getLocatorTransformation(particleEffect.locator());
                if (transformation != null) {
                    ParticleType<?> particleType = BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.tryParse(particleEffect.effect()));
                    if (!(particleType instanceof ParticleOptions particleOptions)) return;
                    Vector4f vec = transformation.matrix().transform(new Vector4f(0, 0, 0, 1));
                    //note: model rotation relative to entity's yaw is happening via transforming PoseStack during rendering
                    // in reality model itself is still facing same direction, so we have to rotate it by ourselves
                    Vector3f pos = new Vector3f(vec.x(), vec.y(), vec.z()).rotate(Axis.YP.rotationDegrees(180 - getYRot()));
                    level().addParticle(
                            particleOptions,
                            pos.x() + getX(),
                            pos.y() + getY(),
                            pos.z() + getZ(),
                            0, 0, 0);
                }
            }
        }

        @Override
        protected void onTimelineEffect(AnimationData.TimelineEffect timelineEffect, BRModel model, BRState state) {

        }
    }
}
