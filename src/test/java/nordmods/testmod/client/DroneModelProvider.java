package nordmods.testmod.client;

import gg.moonflower.pinwheel.api.geometry.GeometryModel;
import gg.moonflower.pinwheel.api.geometry.GeometryModelData;
import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.model.BRModelProvider;
import nordmods.testmod.TestMod;

public class DroneModelProvider implements BRModelProvider<DroneRenderState> {
    private static final Identifier MODEL = Identifier.fromNamespaceAndPath(TestMod.MOD_ID, "drone.geo");
    private static final Identifier ANIMATION = Identifier.fromNamespaceAndPath(TestMod.MOD_ID, "drone.animation");
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(TestMod.MOD_ID, "drone");

    @Override
    public Identifier getModelId(DroneRenderState state) {
        return MODEL;
    }

    @Override
    public Identifier getAnimationId(DroneRenderState state) {
        return ANIMATION;
    }

    @Override
    public Identifier getTextureId(DroneRenderState state) {
        return TEXTURE;
    }
}
