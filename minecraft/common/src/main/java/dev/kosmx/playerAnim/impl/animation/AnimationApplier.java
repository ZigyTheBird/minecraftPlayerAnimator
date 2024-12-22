package dev.kosmx.playerAnim.impl.animation;


import dev.kosmx.playerAnim.api.PartKey;
import dev.kosmx.playerAnim.api.TransformType;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.core.impl.AnimationProcessor;
import dev.kosmx.playerAnim.core.util.MathHelper;
import dev.kosmx.playerAnim.core.util.Pair;
import dev.kosmx.playerAnim.core.util.Vec3f;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public class AnimationApplier extends AnimationProcessor {
    public AnimationApplier(@Nullable IAnimation animation) {
        super(animation);
    }

    public void updatePart(@NotNull PartKey partKey, ModelPart part) {
        Vec3f pos = this.get3DTransform(partKey, TransformType.POSITION, new Vec3f(part.x, part.y, part.z));
        part.x = pos.getX();
        part.y = pos.getY();
        part.z = pos.getZ();
        Vec3f rot = this.get3DTransform(partKey, TransformType.ROTATION, new Vec3f( // clamp guards
                MathHelper.clampToRadian(part.xRot),
                MathHelper.clampToRadian(part.yRot),
                MathHelper.clampToRadian(part.zRot)));
        part.setRotation(rot.getX(), rot.getY(), rot.getZ());
        Vec3f scale = this.get3DTransform(partKey, TransformType.SCALE,
                new Vec3f(part.xScale, part.yScale, part.zScale)
        );
        part.xScale = scale.getX();
        part.yScale = scale.getY();
        part.zScale = scale.getZ();
        if (partKey != PartKey.HEAD) {
            if (partKey == PartKey.TORSO) {
                Pair<Float, Float> torsoBend = getBend(partKey);
                Pair<Float, Float> bodyBend = getBend(PartKey.BODY);
                IBendHelper.INSTANCE.bend(part, new Pair<>(torsoBend.getLeft() + bodyBend.getLeft(), torsoBend.getRight() + bodyBend.getRight()));
            } else {
                IBendHelper.INSTANCE.bend(part, getBend(partKey));
            }
        }
    }

}
