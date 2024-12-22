package dev.kosmx.playerAnim.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.kosmx.playerAnim.api.PartKey;
import dev.kosmx.playerAnim.core.impl.AnimationProcessor;
import dev.kosmx.playerAnim.core.util.SetableSupplier;
import dev.kosmx.playerAnim.impl.Helper;
import dev.kosmx.playerAnim.impl.IMutableModel;
import dev.kosmx.playerAnim.impl.IUpperPartHelper;
import dev.kosmx.playerAnim.impl.animation.IBendHelper;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(HumanoidModel.class)
public abstract class BipedEntityModelMixin<T extends HumanoidRenderState> extends EntityModel<T> implements IMutableModel {
    @Final
    @Shadow
    public ModelPart rightArm;
    @Final
    @Shadow
    public ModelPart leftArm;
    @Unique
    private SetableSupplier<AnimationProcessor> playerAnimator$animation = new SetableSupplier<>(null);

    private BipedEntityModelMixin(Void v, ModelPart modelPart) {
        super(modelPart);
    }

    @Inject(method = "<init>(Lnet/minecraft/client/model/geom/ModelPart;Ljava/util/function/Function;)V", at = @At("RETURN"))
    private void initBend(ModelPart modelPart, Function<ResourceLocation, RenderType> function, CallbackInfo ci){
        IBendHelper.INSTANCE.initBend(modelPart.getChild("body"), Direction.DOWN);
        IBendHelper.INSTANCE.initBend(modelPart.getChild("right_arm"), Direction.UP);
        IBendHelper.INSTANCE.initBend(modelPart.getChild("left_arm"), Direction.UP);
        IBendHelper.INSTANCE.initBend(modelPart.getChild("right_leg"), Direction.UP);
        IBendHelper.INSTANCE.initBend(modelPart.getChild("left_leg"), Direction.UP);
        ((IUpperPartHelper)rightArm).playerAnimator$setUpperPart(true);
        ((IUpperPartHelper)leftArm).playerAnimator$setUpperPart(true);
        ((IUpperPartHelper)head).playerAnimator$setUpperPart(true);
        ((IUpperPartHelper)hat).playerAnimator$setUpperPart(true);
    }

    @Override
    public void playerAnimator$setAnimationSupplier(SetableSupplier<AnimationProcessor> emoteSupplier){
        this.playerAnimator$animation = emoteSupplier;
    }

    @Inject(method = "copyPropertiesTo", at = @At("RETURN"))
    private void copyMutatedAttributes(HumanoidModel<T> bipedEntityModel, CallbackInfo ci){
        if(playerAnimator$animation != null) {
            ((IMutableModel) bipedEntityModel).playerAnimator$setAnimationSupplier(playerAnimator$animation);
        }
    }

    @Intrinsic(displace = true)
    @Override
    public void renderToBuffer(@NotNull PoseStack matrices, @NotNull VertexConsumer vertices, int light, int overlay, int color) {
        if(Helper.isBendEnabled() && this.playerAnimator$animation.get() != null && this.playerAnimator$animation.get().isActive()){

            this.allParts().forEach((part)->{
                if(! ((IUpperPartHelper) part).playerAnimator$isUpperPart()){
                    part.render(matrices, vertices, light, overlay, color);
                }
            });

            SetableSupplier<AnimationProcessor> emoteSupplier = this.playerAnimator$animation;
            matrices.pushPose();
            IBendHelper.rotateMatrixStack(matrices, emoteSupplier.get().getBend(PartKey.BODY));
            this.allParts().forEach((part)->{
                if(((IUpperPartHelper) part).playerAnimator$isUpperPart()){
                    part.render(matrices, vertices, light, overlay, color);
                }
            });
            matrices.popPose();
        } else super.renderToBuffer(matrices, vertices, light, overlay, color);
    }

    @Final
    @Shadow public ModelPart body;

    @Shadow @Final public ModelPart head;

    @Shadow @Final public ModelPart hat;

}
