package nordmods.biscuit_roll.common.animation;

import com.mojang.math.Axis;
import gg.moonflower.molangcompiler.api.exception.MolangRuntimeException;
import gg.moonflower.pinwheel.api.animation.AnimationData;
import gg.moonflower.pinwheel.api.transform.LocatorTransformation;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import nordmods.biscuit_roll.common.model.BRModel;
import nordmods.biscuit_roll.common.state.BRState;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class EntityAnimationController<E extends Entity & BRAnimatedObject> extends BRAnimationController<E> {
    private final EffectConsumer<AnimationData.SoundEffect> soundEffectConsumer;
    private final EffectConsumer<AnimationData.ParticleEffect> particleEffectConsumer;
    public EntityAnimationController(E animatedObject, boolean isClient) {
        super(animatedObject, isClient);
        this.soundEffectConsumer = (effect, model, state) -> {
            try {
                if (animatedObject.level() instanceof ClientLevel clientLevel) {
                    clientLevel.playLocalSound(
                            animatedObject,
                            SoundEvent.createVariableRangeEvent(Identifier.tryParse(effect.effect())),
                            animatedObject.getSoundSource(),
                            getEnvironment().resolve(effect.pitch()),
                            getEnvironment().resolve(effect.volume())
                    );
                }
            } catch (MolangRuntimeException molangRuntimeException) {
                throw (new RuntimeException("Failed to play sound effect " + effect.effect() + " at " + effect.time(), molangRuntimeException));
            }
        };
        this.particleEffectConsumer = (effect, model, state) -> {
            LocatorTransformation transformation = model.getLocatorTransformation(effect.locator());
            if (transformation != null) {
                ParticleType<?> particleType = BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.tryParse(effect.effect()));
                if (!(particleType instanceof ParticleOptions particleOptions)) return;
                Vector4f vec = transformation.matrix().transform(new Vector4f(0,0, 0, 1));
                Vector3f pos = new Vector3f(vec.x(), vec.y(), vec.z()).rotate(Axis.YP.rotationDegrees(180 - animatedObject.getYRot()));
                animatedObject.level().addParticle(
                        particleOptions,
                        pos.x() + animatedObject.getX(),
                        pos.y() + animatedObject.getY(),
                        pos.z() + animatedObject.getZ(),
                        0, 0, 0);
            }
        };
    }

    public EntityAnimationController(E animatedObject) {
        this(animatedObject, animatedObject.level().isClientSide());
    }

    @Override
    protected void onSoundEffect(AnimationData.SoundEffect soundEffect, BRModel model, BRState state) {
        soundEffectConsumer.accept(soundEffect, model, state);
    }

    @Override
    protected void onParticleEffect(AnimationData.ParticleEffect particleEffect, BRModel model, BRState state) {
        particleEffectConsumer.accept(particleEffect, model, state);
    }

    @Override
    protected void onTimelineEffect(AnimationData.TimelineEffect timelineEffect, BRModel model, BRState state) {

    }
}
