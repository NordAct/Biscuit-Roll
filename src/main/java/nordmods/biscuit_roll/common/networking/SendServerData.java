package nordmods.biscuit_roll.common.networking;

import gg.moonflower.pinwheel.api.animation.AnimationData;
import gg.moonflower.pinwheel.api.geometry.GeometryModelData;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.BiscuitRoll;
import nordmods.biscuit_roll.common.model.PolyMeshAttachments;
import nordmods.biscuit_roll.common.resource_managers.ServerPolyMeshAttachmentsManager;
import nordmods.biscuit_roll.common.util.BRStreamCodecs;
import nordmods.biscuit_roll.common.resource_managers.ServerAnimationManager;
import nordmods.biscuit_roll.common.resource_managers.ServerModelManager;

import java.util.HashMap;
import java.util.Map;

public record SendServerData(Map<Identifier, GeometryModelData> modelData, Map<Identifier, AnimationData[]> animationData, Map<Identifier, PolyMeshAttachments> polyMeshAttachments) implements CustomPacketPayload {
    public static final Type<SendServerData> TYPE = new Type<>(BiscuitRoll.id("send_server_data"));
    public static final StreamCodec<FriendlyByteBuf, SendServerData> STREAM_CODEC = new StreamCodec<>() {
        private static final StreamCodec<FriendlyByteBuf, Map<Identifier, GeometryModelData>> GEOMETRY_MODEL_DATA = ByteBufCodecs.map(
                HashMap::new,
                Identifier.STREAM_CODEC,
                BRStreamCodecs.GEOMETRY_MODEL_DATA
        );
        private static final StreamCodec<ByteBuf, Map<Identifier, AnimationData[]>> ANIMATION_DATA = ByteBufCodecs.map(
                HashMap::new,
                Identifier.STREAM_CODEC,
                BRStreamCodecs.ANIMATION_DATA_ARRAY
        );
        private static final StreamCodec<ByteBuf, Map<Identifier, PolyMeshAttachments>> POLY_MESH_ATTACHMENTS = ByteBufCodecs.map(
                HashMap::new,
                Identifier.STREAM_CODEC,
                BRStreamCodecs.POLY_MESH_ATTACHMENTS
        );
        @Override
        public SendServerData decode(FriendlyByteBuf input) {
            Map<Identifier, GeometryModelData> modelData = GEOMETRY_MODEL_DATA.decode(input);
            Map<Identifier, AnimationData[]> animationData = ANIMATION_DATA.decode(input);
            Map<Identifier, PolyMeshAttachments> polyMeshAttachments = POLY_MESH_ATTACHMENTS.decode(input);
            return new SendServerData(modelData, animationData, polyMeshAttachments);
        }

        @Override
        public void encode(FriendlyByteBuf output, SendServerData value) {
            GEOMETRY_MODEL_DATA.encode(output, value.modelData);
            ANIMATION_DATA.encode(output, value.animationData);
            POLY_MESH_ATTACHMENTS.encode(output, value.polyMeshAttachments);
        }
    };
    public SendServerData() {
        this(ServerModelManager.instance().getRegistryRaw(), ServerAnimationManager.instance().getRegistry(), ServerPolyMeshAttachmentsManager.instance().getRegistry());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
