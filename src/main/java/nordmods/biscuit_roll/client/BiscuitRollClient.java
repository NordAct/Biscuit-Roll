package nordmods.biscuit_roll.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientBlockEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.server.packs.PackType;
import nordmods.biscuit_roll.BiscuitRoll;
import nordmods.biscuit_roll.client.resource_managers.ClientAnimationManager;
import nordmods.biscuit_roll.client.resource_managers.ClientModelManager;
import nordmods.biscuit_roll.client.resource_managers.ClientPolyMeshAttachmentsManager;
import nordmods.biscuit_roll.common.BRAttachments;
import nordmods.biscuit_roll.common.animation.BRBlockEntityWithAnimationProcessor;
import nordmods.biscuit_roll.common.animation.BREntityWithAnimationProcessor;
import nordmods.biscuit_roll.common.animation.processor.ControllerState;
import nordmods.biscuit_roll.common.networking.SendServerData;
import nordmods.biscuit_roll.common.resource_managers.ServerAnimationManager;
import nordmods.biscuit_roll.common.resource_managers.ServerModelManager;
import nordmods.biscuit_roll.common.resource_managers.ServerPolyMeshAttachmentsManager;

/// Initializer class for client side
//todo move from FAPI to loader agnostic approach
public class BiscuitRollClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(BiscuitRoll.id("poly_mesh_attachments"), ClientPolyMeshAttachmentsManager.instance());
        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(BiscuitRoll.id("model"), ClientModelManager.instance());
        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(BiscuitRoll.id("animation"), ClientAnimationManager.instance());

        ClientConfigurationNetworking.registerGlobalReceiver(SendServerData.TYPE, ((payload, _) -> {
            ServerModelManager.instance().getRegistry().clear();
            ServerModelManager.instance().getRegistryRaw().clear();
            ServerModelManager.instance().getRegistryRaw().putAll(payload.modelData());

            ServerAnimationManager.instance().getRegistry().clear();
            ServerAnimationManager.instance().getRegistry().putAll(payload.animationData());

            ServerPolyMeshAttachmentsManager.instance().getRegistry().clear();
            ServerPolyMeshAttachmentsManager.instance().getRegistry().putAll(payload.polyMeshAttachments());

            ClientModelManager.instance().clearCache();
            ClientAnimationManager.instance().clearCache();
        }));

        ClientEntityEvents.ENTITY_LOAD.register((entity, _) -> {
            if (entity instanceof BREntityWithAnimationProcessor<?> animationProcessorOwner)
                entity.onAttachedSet(BRAttachments.CONTROLLER_STATES).register((_, newValue) -> {
                    if (newValue != null) {
                        ControllerState.applyControllerStates(newValue, animationProcessorOwner.getAnimationControllers());
                        animationProcessorOwner.onAnimationControllersUpdated();
                    }
            });
        });

        ClientBlockEntityEvents.BLOCK_ENTITY_LOAD.register((entity, _) -> {
            if (entity instanceof BRBlockEntityWithAnimationProcessor<?> animationProcessorOwner)
                entity.onAttachedSet(BRAttachments.CONTROLLER_STATES).register((_, newValue) -> {
                            if (newValue != null) {
                                ControllerState.applyControllerStates(newValue, animationProcessorOwner.getAnimationControllers());
                                animationProcessorOwner.onAnimationControllersUpdated();
                            }
                        });
        });
    }
}
