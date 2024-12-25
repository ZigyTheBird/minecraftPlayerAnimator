package dev.kosmx.playerAnim.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.kosmx.playerAnim.api.PartKey;
import dev.kosmx.playerAnim.impl.Helper;
import dev.kosmx.playerAnim.impl.IPlayerAnimationState;
import dev.kosmx.playerAnim.impl.IUpperPartHelper;
import dev.kosmx.playerAnim.impl.animation.IBendHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * use MixinExtras instead :)
 * high priority
 */
@Mixin(value = LivingEntityRenderer.class, priority = 100)
public abstract class LivingEntityRenderRedirect_bendOnly {

    @WrapOperation(method = "render(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/RenderLayer;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/state/EntityRenderState;FF)V"))
    private void wrapRender(RenderLayer<?, ?> layer, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, EntityRenderState entityRenderState, float f, float g, Operation<Void> original) {
        if (Helper.isBendEnabled() && entityRenderState instanceof IPlayerAnimationState state && state.playerAnimator$getAnimationApplier().isActive() && ((IUpperPartHelper) layer).playerAnimator$isUpperPart()) {
            poseStack.pushPose();
            IBendHelper.rotateMatrixStack(poseStack, state.playerAnimator$getAnimationApplier().getBend(PartKey.BODY));

            original.call(layer, poseStack, multiBufferSource, i, entityRenderState, f, g);

            poseStack.popPose();
        } else {
            original.call(layer, poseStack, multiBufferSource, i, entityRenderState, f, g);
        }
    }


}
