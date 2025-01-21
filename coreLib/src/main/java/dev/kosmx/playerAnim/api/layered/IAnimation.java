package dev.kosmx.playerAnim.api.layered;


import dev.kosmx.playerAnim.api.PartKey;
import dev.kosmx.playerAnim.api.TransformType;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonConfiguration;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import dev.kosmx.playerAnim.core.util.Vec3f;
import org.jetbrains.annotations.NotNull;

/**
 * An entry in {@link AnimationStack}, used to get the animated parts current transform
 */
public interface IAnimation {

    /**
     * Animation tick, on lag free client 20 [tick/sec]
     * You can get the animations time from other places, but it will be invoked when the animation is ACTIVE
     */
    default void tick(){}

    /**
     * Is the animation currently active.
     * Tick will only be invoked when ACTIVE
     * @return active
     */
    boolean isActive();

    /**
     * Get the transformed value to a model part, transform type.
     * @param modelName The questionable model part
     * @param type      Transform type
     * @param tickDelta Time since the last tick. 0-1
     * @param value0    The value before the transform. For identity transform return with it.
     * @return The new transform value
     *
     * @deprecated use {@link IAnimation#get3DTransform(PartKey, TransformType, float, Vec3f)} instead.
     * {@link PartKey} is supposed to be faster than string comparisons.
     */
    @Deprecated(forRemoval = true)
    default @NotNull Vec3f get3DTransform(@NotNull String modelName, @NotNull TransformType type, float tickDelta, @NotNull Vec3f value0) {
        throw new AssertionError("this, of get3DTransform must be implemented.");
    }


    /**
     * Get the transformed value to a model part, transform type.
     * <p>
     * API note <br>
     * DO NOT CALL super.get3DTransform() as the default implementation will be removed.
     * @param modelKey The questionable model part
     * @param type      Transform type
     * @param tickDelta Time since the last tick. 0-1
     * @param value0    The value before the transform. For identity transform return with it.
     * @return The new transform value
     */
    default @NotNull Vec3f get3DTransform(@NotNull PartKey modelKey, @NotNull TransformType type, float tickDelta, @NotNull Vec3f value0) {
        return get3DTransform(modelKey.getKey(), type, tickDelta, value0);
    }


    /**
     * Called before rendering a character
     * @param tickDelta Time since the last tick. 0-1
     */
    void setupAnim(float tickDelta);

    /**
     * Active animation can request first person render mode.
     * @param tickDelta current tickDelta
     * @return {@link FirstPersonMode}
     */
    @NotNull
    default FirstPersonMode getFirstPersonMode(float tickDelta) {
        return FirstPersonMode.NONE;
    }

    /**
     * @param tickDelta
     * @return current first person configuration, only requested when playing this animation
     */
    @NotNull
    default FirstPersonConfiguration getFirstPersonConfiguration(float tickDelta) {
        return new FirstPersonConfiguration();
    }
}
