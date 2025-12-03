package nordmods.testmod.common;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
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

    private static final EntityDataAccessor<Boolean> IS_BROWN = SynchedEntityData.defineId(Dragon.class, EntityDataSerializers.BOOLEAN);
    public void setIsBrown(boolean state) {
        entityData.set(IS_BROWN, state);
    }
    public boolean isBrown() {
        return entityData.get(IS_BROWN);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IS_BROWN, false);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput valueOutput) {
        super.addAdditionalSaveData(valueOutput);
        valueOutput.putBoolean("isBrown", isBrown());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput valueInput) {
        super.readAdditionalSaveData(valueInput);
        setIsBrown(valueInput.getBooleanOr("isBrown", false));
    }

    @Override
    public void addMolangVariables(Context context) {
        context.addQuery("anim_time", tickCount / 20f);
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
            controller.playAnimation(isBrown() ? "walk" : "dance");
        }
    }
}
