package dev.kosmx.animatorTestmod;

import dev.kosmx.playerAnim.api.firstPerson.FirstPersonConfiguration;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier;
import dev.kosmx.playerAnim.api.layered.modifier.MirrorModifier;
import dev.kosmx.playerAnim.api.layered.modifier.SpeedModifier;
import dev.kosmx.playerAnim.core.util.Ease;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationFactory;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * Testmod for testing and demonstration purposes.
 * <hr>
 *
 * In this dev env I use mojmap (the project was remapped to it when I initially began supporting forge)<br>
 * If you want to see what would it like with Yarn,<br>
 * use <code>gradlew migrateMappings --mappings "1.19+build.4"</code> or with the latest mapping<br>
 * More about migrateMappings on <a href="https://fabricmc.net/wiki/tutorial:migratemappings">Fabric wiki</a>
 *
 */
public class PlayerAnimTestmod implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("testmod");
    //public static final ModifierLayer<IAnimation> testAnimation = new ModifierLayer<>(); //Create an animation container for the main player
    //You can create a map for every player or just mixin the data into the playerEntity.

    @Override
    public void onInitializeClient() {
        LOGGER.warn("Testmod is loading :D");

        //You might use the EVENT to register new animations, or you can use Mixin.
        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(ResourceLocation.fromNamespaceAndPath("testmod", "animation"), 42, (player) -> {
            if (player instanceof LocalPlayer) {
                //animationStack.addAnimLayer(42, testAnimation); //Add and save the animation container for later use.
                ModifierLayer<IAnimation> testAnimation =  new ModifierLayer<>();

                testAnimation.addModifierBefore(new SpeedModifier(0.5f)); //This will be slow
                testAnimation.addModifierBefore(new MirrorModifier(true)); //Mirror the animation
                return testAnimation;
            }
            return null;
        });

        PlayerAnimationAccess.REGISTER_ANIMATION_EVENT.register((player, animationStack) -> {
            ModifierLayer<IAnimation> layer = new ModifierLayer<>();
            animationStack.addAnimLayer(69, layer);
            PlayerAnimationAccess.getPlayerAssociatedData(player).set(ResourceLocation.fromNamespaceAndPath("testmod", "test"), layer);
        });
        //You can add modifiers to the ModifierLayer.


    }

    public static void playTestAnimation() {
        //Use this for setting an animation without fade
        //PlayerAnimTestmod.testAnimation.setAnimation(new KeyframeAnimationPlayer(AnimationRegistry.animations.get("two_handed_vertical_right_right")));

        ModifierLayer<IAnimation> testAnimation;
        if (new Random().nextBoolean()) {
            testAnimation = (ModifierLayer<IAnimation>) PlayerAnimationAccess.getPlayerAssociatedData(Minecraft.getInstance().player).get(ResourceLocation.fromNamespaceAndPath("testmod", "animation"));
        } else {
            testAnimation = (ModifierLayer<IAnimation>) PlayerAnimationAccess.getPlayerAssociatedData(Minecraft.getInstance().player).get(ResourceLocation.fromNamespaceAndPath("testmod", "test"));
        }

        if (testAnimation.getAnimation() != null && new Random().nextBoolean()) {
            //It will fade out from the current animation, null as newAnimation means no animation.
            testAnimation.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(20, Ease.LINEAR), null);
        } else {
            //Fade from current animation to a new one.
            //Will not fade if there is no animation currently.
            testAnimation.replaceAnimationWithFade(AbstractFadeModifier.functionalFadeIn(20, (modelName, type, value) -> value),
                    PlayerAnimationRegistry.getAnimation(ResourceLocation.fromNamespaceAndPath("testmod", "two_handed_slash_vertical_right")).playAnimation()
                            .setFirstPersonMode(FirstPersonMode.THIRD_PERSON_MODEL)
                            .setFirstPersonConfiguration(new FirstPersonConfiguration().setShowRightArm(true).setShowLeftItem(false))
            );
        }


    }
}
