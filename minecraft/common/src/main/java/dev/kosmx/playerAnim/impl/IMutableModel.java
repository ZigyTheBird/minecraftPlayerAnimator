package dev.kosmx.playerAnim.impl;

import dev.kosmx.playerAnim.core.impl.AnimationProcessor;
import dev.kosmx.playerAnim.core.util.SetableSupplier;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface IMutableModel {

    void playerAnimator$setAnimationSupplier(SetableSupplier<AnimationProcessor> emoteSupplier);

    SetableSupplier<AnimationProcessor> playerAnimator$getAnimationSupplier();

}
