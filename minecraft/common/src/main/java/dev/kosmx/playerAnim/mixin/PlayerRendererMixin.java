package dev.kosmx.playerAnim.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.kosmx.playerAnim.api.PartKey;
import dev.kosmx.playerAnim.api.TransformType;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import dev.kosmx.playerAnim.core.util.Vec3f;
import dev.kosmx.playerAnim.impl.IAnimatedPlayer;
import dev.kosmx.playerAnim.impl.IPlayerAnimationState;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerRenderState, PlayerModel> {

    public PlayerRendererMixin(EntityRendererProvider.Context context, PlayerModel entityModel, float f, Void v) {
        super(context, entityModel, f);
    }

    @Inject(method = "extractRenderState(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/client/renderer/entity/state/PlayerRenderState;F)V", at = @At("HEAD"))
    private void modifyRenderState(AbstractClientPlayer abstractClientPlayer, PlayerRenderState playerRenderState, float f, CallbackInfo ci) {
        AnimationApplier animation = ((IAnimatedPlayer) abstractClientPlayer).playerAnimator_getAnimation();
        animation.setTickDelta(f);

        ((IPlayerAnimationState)playerRenderState).playerAnimator$setAnimationApplier(animation);
        ((IPlayerAnimationState)playerRenderState).playerAnimator$setLocalPlayer(abstractClientPlayer.isLocalPlayer());
        ((IPlayerAnimationState)playerRenderState).playerAnimator$setLocalPlayer(abstractClientPlayer == Minecraft.getInstance().cameraEntity);
    }

    @Intrinsic(displace = true)
    @Override
    public void render(@NotNull PlayerRenderState entityRenderState, @NotNull PoseStack poseStack, @NotNull MultiBufferSource multiBufferSource, int i) {
        if (FirstPersonMode.isFirstPersonPass() && entityRenderState instanceof IPlayerAnimationState state) {
            var animationApplier = state.playerAnimator$getAnimationApplier();
            var config = animationApplier.getFirstPersonConfiguration();

            if (state.playerAnimator$isCameraEntity()) {
                // Hiding all parts, because they should not be visible in first person
                playerAnimator$setAllPartsVisible(false);
                // Showing arms based on configuration
                var showRightArm = config.isShowRightArm();
                var showLeftArm = config.isShowLeftArm();
                this.model.rightArm.visible = showRightArm;
                this.model.rightSleeve.visible = showRightArm;
                this.model.leftArm.visible = showLeftArm;
                this.model.leftSleeve.visible = showLeftArm;
            }
        }

        // No `else` case needed to show parts, since the default state should be correct already
    }

    @Unique
    private void playerAnimator$setAllPartsVisible(boolean visible) {
        this.model.head.visible = visible;
        this.model.body.visible = visible;
        this.model.leftLeg.visible = visible;
        this.model.rightLeg.visible = visible;
        this.model.rightArm.visible = visible;
        this.model.leftArm.visible = visible;

        this.model.hat.visible = visible;
        this.model.leftSleeve.visible = visible;
        this.model.rightSleeve.visible = visible;
        this.model.leftPants.visible = visible;
        this.model.rightPants.visible = visible;
        this.model.jacket.visible = visible;
    }


    @Inject(method = "setupRotations(Lnet/minecraft/client/renderer/entity/state/PlayerRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;FF)V", at = @At("RETURN"))
    private void applyBodyTransforms(PlayerRenderState playerRenderState, PoseStack matrixStack, float f, float g, CallbackInfo ci){
        var animationPlayer = ((IPlayerAnimationState) playerRenderState).playerAnimator$getAnimationApplier();
        if(animationPlayer.isActive()){

            //These are additive properties
            Vec3f vec3e = animationPlayer.get3DTransform(PartKey.BODY, TransformType.SCALE,
                    new Vec3f(ModelPart.DEFAULT_SCALE, ModelPart.DEFAULT_SCALE, ModelPart.DEFAULT_SCALE)
            );
            matrixStack.scale(vec3e.getX(), vec3e.getY(), vec3e.getZ());
            Vec3f vec3d = animationPlayer.get3DTransform(PartKey.BODY, TransformType.POSITION, Vec3f.ZERO);
            matrixStack.translate(vec3d.getX(), vec3d.getY() + 0.7, vec3d.getZ());
            Vec3f vec3f = animationPlayer.get3DTransform(PartKey.BODY, TransformType.ROTATION, Vec3f.ZERO);
            matrixStack.mulPose(Axis.ZP.rotation(vec3f.getZ()));    //roll
            matrixStack.mulPose(Axis.YP.rotation(vec3f.getY()));    //pitch
            matrixStack.mulPose(Axis.XP.rotation(vec3f.getX()));    //yaw
            matrixStack.translate(0, - 0.7d, 0);
        }
    }
}
