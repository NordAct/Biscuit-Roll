package nordmods.biscuit_roll.common.networking;

import gg.moonflower.pinwheel.api.animation.AnimationData;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import nordmods.biscuit_roll.BiscuitRoll;
import nordmods.biscuit_roll.common.util.BRStreamCodecs;
import nordmods.biscuit_roll.common.util.ServerAnimationManager;

import java.util.HashMap;
import java.util.Map;

public record SendServerAnimationData( Map<Identifier, AnimationData[]> animationData) implements CustomPacketPayload {
    public static final Type<SendServerAnimationData> TYPE = new Type<>(BiscuitRoll.id("send_server_animation_data"));
    public static final StreamCodec<FriendlyByteBuf, SendServerAnimationData> STREAM_CODEC = new StreamCodec<>() {
        private static final StreamCodec<ByteBuf, Map<Identifier, AnimationData[]>> ANIMATION_DATA = ByteBufCodecs.map(
                HashMap::new,
                Identifier.STREAM_CODEC,
                BRStreamCodecs.ANIMATION_DATA_ARRAY
        );
        @Override
        public SendServerAnimationData decode(FriendlyByteBuf input) {
            Map<Identifier, AnimationData[]> animationData = ANIMATION_DATA.decode(input);
            return new SendServerAnimationData(animationData);
        }

        @Override
        public void encode(FriendlyByteBuf output, SendServerAnimationData value) {
            ANIMATION_DATA.encode(output, value.animationData);
        }
    };
    public SendServerAnimationData() {
        this(ServerAnimationManager.instance().getRegistry());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
