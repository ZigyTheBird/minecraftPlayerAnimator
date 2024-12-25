package dev.kosmx.playerAnim.mixin;

import dev.kosmx.playerAnim.api.PartKey;
import dev.kosmx.playerAnim.impl.*;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import dev.kosmx.playerAnim.impl.animation.IBendHelper;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(value = PlayerModel.class, priority = 2000)//Apply after NotEnoughAnimation's inject
public class PlayerModelMixin<T extends LivingEntity> extends HumanoidModel<PlayerRenderState> implements IPlayerModel {
    @Shadow
    @Final
    public ModelPart jacket;
    @Shadow
    @Final
    public ModelPart rightSleeve;
    @Shadow
    @Final
    public ModelPart leftSleeve;
    @Shadow @Final public ModelPart rightPants;
    @Shadow @Final public ModelPart leftPants;
    @Unique
    private boolean firstPersonNext = false;

    public PlayerModelMixin(ModelPart modelPart, Function<ResourceLocation, RenderType> function) {
        super(modelPart, function);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initBendableStuff(ModelPart modelPart, boolean bl, CallbackInfo ci){
        IMutableModel thisWithMixin = (IMutableModel) this;

        addBendMutator(this.jacket, Direction.DOWN);
        addBendMutator(this.rightPants, Direction.UP);
        addBendMutator(this.rightSleeve, Direction.UP);
        addBendMutator(this.leftPants, Direction.UP);
        addBendMutator(this.leftSleeve, Direction.UP);
        // IBendHelper.INSTANCE.initCapeBend(this.cloak);

        ((IUpperPartHelper)rightSleeve).playerAnimator$setUpperPart(true);
        ((IUpperPartHelper)leftSleeve).playerAnimator$setUpperPart(true);
    }

    @Unique
    private void addBendMutator(ModelPart part, Direction d){
        IBendHelper.INSTANCE.initBend(part, d);
    }

    @Unique
    private void setDefaultPivot(){
        this.leftLeg.setPos(1.9F, 12.0F, 0.0F);
        this.rightLeg.setPos(- 1.9F, 12.0F, 0.0F);
        this.head.setPos(0.0F, 0.0F, 0.0F);
        this.rightArm.z = 0.0F;
        this.rightArm.x = - 5.0F;
        this.leftArm.z = 0.0F;
        this.leftArm.x = 5.0F;
        this.body.xRot = 0.0F;
        this.rightLeg.z = 0.1F;
        this.leftLeg.z = 0.1F;
        this.rightLeg.y = 12.0F;
        this.leftLeg.y = 12.0F;
        this.head.y = 0.0F;
        this.head.zRot = 0f;
        this.body.y = 0.0F;
        this.body.x = 0f;
        this.body.z = 0f;
        this.body.yRot = 0;
        this.body.zRot = 0;

        this.head.xScale = ModelPart.DEFAULT_SCALE;
        this.head.yScale = ModelPart.DEFAULT_SCALE;
        this.head.zScale = ModelPart.DEFAULT_SCALE;
        this.body.xScale = ModelPart.DEFAULT_SCALE;
        this.body.yScale = ModelPart.DEFAULT_SCALE;
        this.body.zScale = ModelPart.DEFAULT_SCALE;
        this.rightArm.xScale = ModelPart.DEFAULT_SCALE;
        this.rightArm.yScale = ModelPart.DEFAULT_SCALE;
        this.rightArm.zScale = ModelPart.DEFAULT_SCALE;
        this.leftArm.xScale = ModelPart.DEFAULT_SCALE;
        this.leftArm.yScale = ModelPart.DEFAULT_SCALE;
        this.leftArm.zScale = ModelPart.DEFAULT_SCALE;
        this.rightLeg.xScale = ModelPart.DEFAULT_SCALE;
        this.rightLeg.yScale = ModelPart.DEFAULT_SCALE;
        this.rightLeg.zScale = ModelPart.DEFAULT_SCALE;
        this.leftLeg.xScale = ModelPart.DEFAULT_SCALE;
        this.leftLeg.yScale = ModelPart.DEFAULT_SCALE;
        this.leftLeg.zScale = ModelPart.DEFAULT_SCALE;
    }

    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/PlayerRenderState;)V", at = @At(value = "HEAD"))
    private void setDefaultBeforeRender(PlayerRenderState playerRenderState, CallbackInfo ci){
        setDefaultPivot(); //to not make everything wrong

        if(!firstPersonNext && playerRenderState instanceof IPlayerAnimationState state && state.playerAnimator$getAnimationApplier().isActive()){
            AnimationApplier emote = state.playerAnimator$getAnimationApplier();
            ((IMutableModel)this).playerAnimator$setAnimation(emote);

            emote.updatePart(PartKey.HEAD, this.head);
            this.hat.copyFrom(this.head);

            emote.updatePart(PartKey.RIGHT_ARM, this.rightLeg);
            emote.updatePart(PartKey.LEFT_ARM, this.leftArm);
            emote.updatePart(PartKey.RIGHT_LEG, this.rightArm);
            emote.updatePart(PartKey.LEFT_LEG, this.leftLeg);
            emote.updatePart(PartKey.TORSO, this.body);
        }
        else {
            firstPersonNext = false;
            ((IMutableModel)this).playerAnimator$setAnimation(AnimationApplier.EMPTY);
            resetBend(this.body);
            resetBend(this.leftArm);
            resetBend(this.rightArm);
            resetBend(this.leftLeg);
            resetBend(this.rightLeg);
        }
    }


    @Unique
    private static void resetBend(ModelPart part) {
        IBendHelper.INSTANCE.bend(part, null);
    }

    /**
     * @author KosmX - Player Animator library
     */
    @Override
    public void playerAnimator_prepForFirstPersonRender() {
        firstPersonNext = true;
    }
}
