package dev.kosmx.playerAnim.mixin;

import net.minecraft.client.model.PlayerModel;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerModel.class)
public interface PlayerModelAccessor {
    // @Accessor
    // ModelPart getCloak();
}
