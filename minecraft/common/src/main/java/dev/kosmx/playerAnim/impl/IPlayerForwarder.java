package dev.kosmx.playerAnim.impl;

import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public interface IPlayerForwarder<T extends AbstractClientPlayer & IAnimatedPlayer> {
    default T playerAnimator$getAnimatedPlayer() {
        return null;
    }

    default void playerAnimator$setAnimatedPlayer(AbstractClientPlayer player) {
    }

    static boolean isLocalPlayer(EntityRenderState renderState) {
        return renderState instanceof IPlayerForwarder<?> forwarder &&
                forwarder.playerAnimator$getAnimatedPlayer() instanceof LocalPlayer;
    }

    static boolean isPlayer(EntityRenderState renderState) {
        return renderState instanceof IPlayerForwarder<?> forwarder &&
                forwarder.playerAnimator$getAnimatedPlayer() instanceof Player;
    }

    static @Nullable AnimationApplier getApplier(EntityRenderState renderState) {
        if (renderState instanceof IPlayerForwarder<?> forwarder) {
            IAnimatedPlayer animatedPlayer = forwarder.playerAnimator$getAnimatedPlayer();
            if (animatedPlayer == null) {
                return null;
            }

            return animatedPlayer.playerAnimator_getAnimation();
        }

        return null;
    }
}
