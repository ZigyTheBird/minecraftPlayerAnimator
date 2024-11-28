package dev.kosmx.playerAnim.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.kosmx.playerAnim.core.util.Pair;
import dev.kosmx.playerAnim.impl.IAnimatedPlayer;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CapeLayer.class)
public abstract class CapeLayerMixin extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    public CapeLayerMixin(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderLayerParent) {
        super(renderLayerParent);
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/player/AbstractClientPlayer;FFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/PlayerModel;renderCloak(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;II)V"))
    private void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, AbstractClientPlayer abstractClientPlayer, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        AnimationApplier emote = ((IAnimatedPlayer) abstractClientPlayer).playerAnimator_getAnimation();
        if (emote.isActive()) {
            ModelPart torso = this.getParentModel().body;
            Pair<Float, Float> bend = emote.getBend("torso");

            poseStack.translate(torso.x / 16, torso.y / 16, torso.z / 16);
            poseStack.mulPose((new Quaternionf()).rotateXYZ(torso.xRot, torso.yRot, torso.zRot));
            if (bend.getRight() != 0) {
                poseStack.translate(0, 0.375F * torso.yScale, 0);
                poseStack.mulPose(Axis.XP.rotation(bend.getRight()));
                poseStack.translate(0, -0.375F * torso.yScale, 0);
            }
            poseStack.translate(0.0F, 0.0F, 0.125F);

            double d = Mth.lerp(h, abstractClientPlayer.xCloakO, abstractClientPlayer.xCloak)
                    - Mth.lerp(h, abstractClientPlayer.xo, abstractClientPlayer.getX());
            double m = Mth.lerp(h, abstractClientPlayer.zCloakO, abstractClientPlayer.zCloak)
                    - Mth.lerp(h, abstractClientPlayer.zo, abstractClientPlayer.getZ());
            float n = Mth.rotLerp(h, abstractClientPlayer.yBodyRotO, abstractClientPlayer.yBodyRot);
            double o = Mth.sin(n * (float) (Math.PI / 180.0));
            double p = -Mth.cos(n * (float) (Math.PI / 180.0));
            float s = (float) (d * p - m * o) * 100.0F;
            s = Mth.clamp(s, -20.0F, 20.0F);

            poseStack.mulPose(Axis.XP.rotationDegrees(6.0F / 2.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - s / 2.0F));

            ModelPart cape = ((PlayerModelAccessor)this.getParentModel()).getCloak();
            cape.x = 0;
            cape.y = 0;
            cape.z = 0;
        }
    }

    @WrapWithCondition(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/player/AbstractClientPlayer;FFFFFF)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionf;)V"))
    private boolean mulPose(PoseStack instance, Quaternionf quaternionf, @Local(argsOnly = true) AbstractClientPlayer abstractClientPlayer) {
        AnimationApplier emote = ((IAnimatedPlayer) abstractClientPlayer).playerAnimator_getAnimation();
        return !emote.isActive();
    }

    @WrapWithCondition(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/player/AbstractClientPlayer;FFFFFF)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V"))
    private boolean translate(PoseStack instance, float f, float g, float h, @Local(argsOnly = true) AbstractClientPlayer abstractClientPlayer) {
        AnimationApplier emote = ((IAnimatedPlayer) abstractClientPlayer).playerAnimator_getAnimation();
        return !emote.isActive();
    }
}
