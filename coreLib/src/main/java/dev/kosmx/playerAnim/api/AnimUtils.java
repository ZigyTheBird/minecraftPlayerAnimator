package dev.kosmx.playerAnim.api;

import dev.kosmx.playerAnim.api.layered.AnimationStack;

public abstract class AnimUtils {

    /**
     * Get the animation stack for a player entity on the client.
     * <p>
     * Or you can use {@code ((IPlayer) player).getAnimationStack();}
     *
     * @param player The ClientPlayer object
     * @return The players' animation stack
     */
    public static AnimationStack getPlayerAnimLayer(Object player) throws IllegalArgumentException {
        if (player instanceof IPlayer) {
            return ((IPlayer) player).playerAnimator$getAnimationStack();
        } else throw new IllegalArgumentException(player + " is not a player or library mixins failed");
    }
}
