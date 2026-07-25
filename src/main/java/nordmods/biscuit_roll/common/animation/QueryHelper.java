package nordmods.biscuit_roll.common.animation;

import gg.moonflower.molangcompiler.api.MolangEnvironmentBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PlayerRideableJumping;

/// Util class with a collection of methods made to simplify query application process
public final class QueryHelper { //todo add other queries
    public static void addIsSwimming(MolangEnvironmentBuilder<?> builder, Entity entity) {
        builder.setQuery("is_swimming", entity.isSwimming() ? 1 : 0);
    }

    public static void addSwimAmount(MolangEnvironmentBuilder<?> builder, LivingEntity entity, float tickDelta) {
        builder.setQuery("swim_amount", entity.getSwimAmount(tickDelta));
    }

    public static void addCanClimb(MolangEnvironmentBuilder<?> builder, LivingEntity entity) {
        builder.setQuery("can_climb", entity.onClimbable() ? 1 : 0);
    }

    public static void addCanFly(MolangEnvironmentBuilder<?> builder, LivingEntity entity) {
        builder.setQuery("can_fly", entity.canGlide() ? 1 : 0);
    }

    public static void addBlocking(MolangEnvironmentBuilder<?> builder, LivingEntity entity) {
        builder.setQuery("blocking", entity.isBlocking() ? 1 : 0);
    }

    public static void addCanDamageNearbyMobs(MolangEnvironmentBuilder<?> builder, Mob entity) {
        builder.setQuery("can_damage_nearby_mobs", !entity.level().getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(5), entity::canAttack).isEmpty() ? 1 : 0);
    }

    public static void addPowerJump(MolangEnvironmentBuilder<?> builder, PlayerRideableJumping entity) {
        builder.setQuery("can_power_jump", entity.canJump() ? 1 : 0);
    }

    public static void addBodyXRotation(MolangEnvironmentBuilder<?> builder, Entity entity, float tickDelta) {
        builder.setQuery("body_x_rotation", entity.getXRot(tickDelta));
    }

    public static void addBodyYRotation(MolangEnvironmentBuilder<?> builder, Entity entity, float tickDelta) {
        builder.setQuery("body_y_rotation", entity.getPreciseBodyRotation(tickDelta));
    }

    public static void addHeadXRotation(MolangEnvironmentBuilder<?> builder, Entity entity, float tickDelta) {
        builder.setQuery("head_x_rotation", entity.getViewXRot(tickDelta));
    }

    public static void addHeadYRotation(MolangEnvironmentBuilder<?> builder, Entity entity, float tickDelta) {
        builder.setQuery("head_y_rotation", entity.getViewYRot(tickDelta));
    }
}
