package dev.kosmx.playerAnim.mixin.firstPerson;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import dev.kosmx.playerAnim.impl.IAnimatedPlayer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Inject(method = "collectVisibleEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;isDetached()Z"))
    private void fakeThirdPersonMode(Camera camera, Frustum frustum, List<Entity> list, CallbackInfoReturnable<Boolean> cir, @Share(value = "playeranim$defaultCameraState") LocalBooleanRef defaultCameraState) {
        // mods may need to redirect that method, I want to avoid compatibility issues as long as possible
        defaultCameraState.set(camera.isDetached());
        if (camera.getEntity() instanceof IAnimatedPlayer player && player.playerAnimator_getAnimation().getFirstPersonMode() == FirstPersonMode.THIRD_PERSON_MODEL) {
            FirstPersonMode.setFirstPersonPass(!camera.isDetached() && (!(camera.getEntity() instanceof LivingEntity) || !((LivingEntity)camera.getEntity()).isSleeping())); // this will cause a lot of pain
            ((CameraAccessor)camera).setDetached(true);
        }
    }

    @Inject(method = "collectVisibleEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;isDetached()Z", shift = At.Shift.AFTER))
    private void resetThirdPerson(Camera camera, Frustum frustum, List<Entity> list, CallbackInfoReturnable<Boolean> cir, @Share(value = "playeranim$defaultCameraState") LocalBooleanRef defaultCameraState) {
        ((CameraAccessor)camera).setDetached(defaultCameraState.get());
    }

    @Inject(method = "renderEntity", at = @At("TAIL"))
    private void dontRenderEntity_End(Entity entity, double cameraX, double cameraY, double cameraZ,
                                      float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, CallbackInfo ci) {
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        if (entity == camera.getEntity()) {
            FirstPersonMode.setFirstPersonPass(false); // Unmark this render cycle
        }
    }
}
