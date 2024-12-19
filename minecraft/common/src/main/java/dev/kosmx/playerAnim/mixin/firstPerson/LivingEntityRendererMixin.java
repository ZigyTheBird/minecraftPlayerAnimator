package dev.kosmx.playerAnim.mixin.firstPerson;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import dev.kosmx.playerAnim.impl.IPlayerForwarder;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.PlayerItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(value = LivingEntityRenderer.class, priority = 2000)
public class LivingEntityRendererMixin {
    @Shadow
    @Final
    protected List<RenderLayer<?, ?>> layers;

    @WrapOperation(
            method = "render(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
    at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;layers:Ljava/util/List;", opcode = Opcodes.GETFIELD))
    private List<RenderLayer<?, ?>> filterLayers(LivingEntityRenderer<?, ?, ?> instance, Operation<List<RenderLayer<?, ?>>> original, @Local(argsOnly = true) LivingEntityRenderState state) {
        if (IPlayerForwarder.isLocalPlayer(state) && FirstPersonMode.isFirstPersonPass()) {
            return layers.stream().filter(layer -> layer instanceof PlayerItemInHandLayer).toList();
        } else return original.call(instance);
    }
}
