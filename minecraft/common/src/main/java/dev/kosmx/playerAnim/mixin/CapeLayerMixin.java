package dev.kosmx.playerAnim.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.kosmx.playerAnim.impl.IPlayerForwarder;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import dev.kosmx.playerAnim.impl.animation.IBendHelper;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CapeLayer.class)
public abstract class CapeLayerMixin extends RenderLayer<PlayerRenderState, PlayerModel> {
    @Shadow @Final private HumanoidModel<PlayerRenderState> model;

    public CapeLayerMixin(RenderLayerParent<PlayerRenderState, PlayerModel> renderLayerParent) {
        super(renderLayerParent);
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/state/PlayerRenderState;FF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/HumanoidModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;II)V"))
    private void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, PlayerRenderState playerRenderState, float f, float g, CallbackInfo ci) {
        AnimationApplier emote = IPlayerForwarder.getApplier(playerRenderState);
        if (emote.isActive()) {
            ModelPart torso = this.getParentModel().body;

            poseStack.translate(torso.x / 16, torso.y / 16, torso.z / 16);
            poseStack.mulPose((new Quaternionf()).rotateXYZ(torso.xRot, torso.yRot, torso.zRot));
            IBendHelper.rotateMatrixStack(poseStack, emote.getBend("torso"));
            poseStack.translate(0.0F, 0.0F, 0.125F);
            poseStack.mulPose(Axis.YP.rotationDegrees(180));

            ModelPart cape = ((PlayerModelAccessor)this.getParentModel()).getCape();
            cape.x = 0;
            cape.y = 0;
            cape.z = 0;
            cape.xRot = 0;
            cape.yRot = 0;
            cape.zRot = 0;
        }
    }

    @WrapWithCondition(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/state/PlayerRenderState;FF)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V"))
    private boolean translate(PoseStack instance, float f, float g, float h, @Local(argsOnly = true) PlayerRenderState playerRenderState) {
        AnimationApplier emote = IPlayerForwarder.getApplier(playerRenderState);
        return emote == null || !emote.isActive();
    }
}
