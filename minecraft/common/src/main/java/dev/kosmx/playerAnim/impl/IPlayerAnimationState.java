package dev.kosmx.playerAnim.impl;

import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Extension of PlayerRenderState
 */
public interface IPlayerAnimationState {

    // bool isLocalPlayer
    boolean playerAnimator$isLocalPlayer();
    void playerAnimator$setLocalPlayer(boolean value);

    // AnimationApplier animationApplier
    void playerAnimator$setAnimationApplier(AnimationApplier value);
    @NotNull AnimationApplier playerAnimator$getAnimationApplier();
}
