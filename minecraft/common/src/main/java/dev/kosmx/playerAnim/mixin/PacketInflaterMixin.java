package dev.kosmx.playerAnim.mixin;

import net.minecraft.network.CompressionDecoder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = CompressionDecoder.class, priority = 999)
public class PacketInflaterMixin {
    @ModifyConstant(method = "decode",constant = @Constant(intValue = CompressionDecoder.MAXIMUM_UNCOMPRESSED_LENGTH))
    private int xlPackets(int old) {
        return 2000000000;
    }
}
