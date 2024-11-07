package dev.kosmx.playerAnim.mixin;

import dev.kosmx.playerAnim.IExampleAnimatedPlayer;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.core.util.Ease;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {
    @Inject(method = "use", at = @At("HEAD"))
    private void playAnimation(Level level, Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        //We must start the animation on client-side
        if (level.isClientSide) {
            var itemStack = player.getItemInHand(interactionHand);
            //Do some filtering
            if (itemStack.getItem().equals(Items.PAPER)) {
                //If we want to play the animation, get the animation container
                var animationContainer = ((IExampleAnimatedPlayer) player).modid_getModAnimation();

                //Use setAnimation to set the current animation. It will be played automatically.
                KeyframeAnimation anim = (KeyframeAnimation) PlayerAnimationRegistry.getAnimation(ResourceLocation.fromNamespaceAndPath("player-animator", "sneak_idle"));

                animationContainer.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(5, Ease.INOUTSINE), new KeyframeAnimationPlayer(anim));

                //Use animationContainer.replaceAnimationWithFade(); to create fading effects instead of sudden changes.
            }
            if (itemStack.getItem().equals(Items.BLUE_ICE)) {
                //If we want to play the animation, get the animation container
                var animationContainer = ((IExampleAnimatedPlayer) player).modid_getModAnimation();

                //Use setAnimation to set the current animation. It will be played automatically.
                KeyframeAnimation anim = (KeyframeAnimation) PlayerAnimationRegistry.getAnimation(ResourceLocation.fromNamespaceAndPath("player-animator", "ice_run"));

                animationContainer.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(5, Ease.INOUTSINE), new KeyframeAnimationPlayer(anim));

                //Use animationContainer.replaceAnimationWithFade(); to create fading effects instead of sudden changes.
            }
            if (itemStack.getItem().equals(Items.QUARTZ)) {
                //If we want to play the animation, get the animation container
                var animationContainer = ((IExampleAnimatedPlayer) player).modid_getModAnimation();

                //Use setAnimation to set the current animation. It will be played automatically.
                KeyframeAnimation anim = (KeyframeAnimation) PlayerAnimationRegistry.getAnimation(ResourceLocation.fromNamespaceAndPath("player-animator", "on_fence_idle_animation"));

                animationContainer.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(5, Ease.INOUTSINE), new KeyframeAnimationPlayer(anim));

                //Use animationContainer.replaceAnimationWithFade(); to create fading effects instead of sudden changes.
            }
            if (itemStack.getItem().equals(Items.GOLD_INGOT)) {
                //If we want to play the animation, get the animation container
                var animationContainer = ((IExampleAnimatedPlayer) player).modid_getModAnimation();

                //Use setAnimation to set the current animation. It will be played automatically.
                KeyframeAnimation anim = (KeyframeAnimation) PlayerAnimationRegistry.getAnimation(ResourceLocation.fromNamespaceAndPath("player-animator", "shield_animation"));

                animationContainer.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(5, Ease.INOUTSINE), new KeyframeAnimationPlayer(anim));

                //Use animationContainer.replaceAnimationWithFade(); to create fading effects instead of sudden changes.
            }
        }
    }
}
