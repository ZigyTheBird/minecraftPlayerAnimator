package dev.kosmx.playerAnim.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.kosmx.playerAnim.impl.IPlayerForwarder;
import dev.kosmx.playerAnim.impl.IUpperPartHelper;
import dev.kosmx.playerAnim.impl.Helper;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import dev.kosmx.playerAnim.impl.animation.IBendHelper;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.List;

/**
 * Compatibility issue: can not redirect {@link RenderLayer#render(PoseStack, MultiBufferSource, int, EntityRenderState, float, float)}
 * I have to modify the matrixStack and do not forget to POP it!
 * <p>
 * I can inject into the enhanced for
 * {@link List#iterator()}      //initial push to keep in sync
 * {@link Iterator#hasNext()}   //to pop the matrix stack
 * {@link Iterator#next()}      //I can see the modelPart, decide if I need to manipulate it. But push always
 *
 * @param <T>
 * @param <M>
 */
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRenderRedirect_bendOnly<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> extends EntityRenderer<T, S> implements RenderLayerParent<S, M> {

    protected LivingEntityRenderRedirect_bendOnly(EntityRendererProvider.Context context) {
        super(context);
    }

    @Inject(method = "render(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
        at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"))
    private void initialPush(S livingEntityRenderState, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci){
        if (Helper.isBendEnabled()) poseStack.pushPose();
    }

    @Inject(method = "render(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
        at = @At(value = "INVOKE", target = "Ljava/util/Iterator;hasNext()Z"))
    private void popMatrixStack(S livingEntityRenderState, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci){
        if (Helper.isBendEnabled()) poseStack.popPose();
    }

    @WrapOperation(method = "render(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
        at = @At(value = "INVOKE", target = "Ljava/util/Iterator;next()Ljava/lang/Object;"))
    private Object transformMatrixStack(Iterator<RenderLayer<S, M>> instance, Operation<RenderLayer<S, M>> original, @Local(argsOnly = true) PoseStack poseStack, @Local(argsOnly = true) LivingEntityRenderState state){
        if (Helper.isBendEnabled()) {
            poseStack.pushPose();
            RenderLayer<S, M> layer = original.call(instance);
            AnimationApplier applier = IPlayerForwarder.getApplier(state);
            if (applier != null && applier.isActive() && ((IUpperPartHelper) layer).isUpperPart()) {
                IBendHelper.rotateMatrixStack(poseStack, applier.getBend("body"));
            }
            return layer;
        } else {
            return original.call(instance);
        }
    }
}
