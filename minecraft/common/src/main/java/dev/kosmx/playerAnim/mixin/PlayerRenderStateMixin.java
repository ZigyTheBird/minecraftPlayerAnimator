package dev.kosmx.playerAnim.mixin;

import dev.kosmx.playerAnim.impl.IAnimatedPlayer;
import dev.kosmx.playerAnim.impl.IPlayerForwarder;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerRenderState.class)
public class PlayerRenderStateMixin<T extends AbstractClientPlayer & IAnimatedPlayer> implements IPlayerForwarder<T> {
    @Unique
    private T playerAnimator$animatedPlayer;

    @Override
    public T playerAnimator$getAnimatedPlayer() {
        return this.playerAnimator$animatedPlayer;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void playerAnimator$setAnimatedPlayer(AbstractClientPlayer player) {
        this.playerAnimator$animatedPlayer = (T) player;
    }
}
