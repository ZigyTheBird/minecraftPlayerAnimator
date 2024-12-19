package dev.kosmx.playerAnim.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.kosmx.playerAnim.api.TransformType;
import dev.kosmx.playerAnim.core.util.Pair;
import dev.kosmx.playerAnim.core.util.Vec3f;
import dev.kosmx.playerAnim.impl.Helper;
import dev.kosmx.playerAnim.impl.IPlayerForwarder;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandLayer.class)
public class HeldItemMixin {

    @Inject(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionf;)V", ordinal = 0))
    private void renderMixin(LivingEntityRenderState livingEntityRenderState, @Nullable BakedModel bakedModel, ItemStack stack, ItemDisplayContext itemDisplayContext, HumanoidArm arm, PoseStack matrices, MultiBufferSource vertexConsumers, int light, CallbackInfo ci){
        AnimationApplier anim = IPlayerForwarder.getApplier(livingEntityRenderState);
        if(Helper.isBendEnabled() && anim != null && anim.isActive()){
            Vec3f data = anim.get3DTransform(arm == HumanoidArm.LEFT ? "leftArm" : "rightArm", TransformType.BEND, new Vec3f(0f, 0f, 0f));

            Pair<Float, Float> pair = new Pair<>(data.getX(), data.getY());

            float offset = 0.25f;
            matrices.translate(0, offset, 0);
            float bend = pair.getRight();
            float axisf = - pair.getLeft();
            Vector3f axis = new Vector3f((float) Math.cos(axisf), 0, (float) Math.sin(axisf));
            //return this.setRotation(axis.getRadialQuaternion(bend));
            matrices.mulPose(new Quaternionf().rotateAxis(bend, axis));
            matrices.translate(0, - offset, 0);

        }
    }

    @Inject(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V"))
    private void changeItemLocation(LivingEntityRenderState livingEntityRenderState, @Nullable BakedModel bakedModel, ItemStack itemStack, ItemDisplayContext itemDisplayContext, HumanoidArm arm, PoseStack matrices, MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
        AnimationApplier anim = IPlayerForwarder.getApplier(livingEntityRenderState);
        if (anim != null && anim.isActive()) {
            Vec3f scale = anim.get3DTransform(arm == HumanoidArm.LEFT ? "leftItem" : "rightItem", TransformType.SCALE,
                    new Vec3f(ModelPart.DEFAULT_SCALE, ModelPart.DEFAULT_SCALE, ModelPart.DEFAULT_SCALE)
            );
            Vec3f rot = anim.get3DTransform(arm == HumanoidArm.LEFT ? "leftItem" : "rightItem", TransformType.ROTATION, Vec3f.ZERO);
            Vec3f pos = anim.get3DTransform(arm == HumanoidArm.LEFT ? "leftItem" : "rightItem", TransformType.POSITION, Vec3f.ZERO).scale(1/16f);

            matrices.scale(scale.getX(), scale.getY(), scale.getZ());
            matrices.translate(pos.getX(), pos.getY(), pos.getZ());

            matrices.mulPose(Axis.ZP.rotation(rot.getZ()));    //roll
            matrices.mulPose(Axis.YP.rotation(rot.getY()));    //pitch
            matrices.mulPose(Axis.XP.rotation(rot.getX()));    //yaw
        }
    }
}
