package dev.visfox.thaumichorizons.lib;

import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;

public final class EntityRay {
    private EntityRay() {
    }

    public static @Nullable Entity getPointedEntity(Level world, LivingEntity entityplayer, double range) {
        Entity pointedEntity = null;
        Vec3 vec3d = new Vec3(entityplayer.getX(), entityplayer.getY() + entityplayer.getEyeHeight(),
            entityplayer.getZ());
        Vec3 look = entityplayer.getViewVector(1.0F);
        Vec3 vec3d3 = vec3d.add(look.scale(range));
        double border = 1.1;
        List<Entity> list = world.getEntities(entityplayer,
            entityplayer.getBoundingBox().expandTowards(look.scale(range)).inflate(border), e -> e.canBeCollidedWith());
        double d2 = 0.0;
        for (Entity entity : list) {
            HitResult clip = world.clip(new ClipContext(
                new Vec3(entityplayer.getX(), entityplayer.getY() + entityplayer.getEyeHeight(), entityplayer.getZ()),
                new Vec3(entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ()),
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entityplayer));
            if (clip.getType() == HitResult.Type.MISS) {
                AABB box = entity.getBoundingBox().inflate(Math.max(0.8F, 0.8F));
                java.util.Optional<Vec3> intercept = box.clip(vec3d, vec3d3);
                if (box.contains(vec3d)) {
                    if (0.0 < d2 || d2 == 0.0) {
                        pointedEntity = entity;
                        d2 = 0.0;
                    }
                } else if (intercept.isPresent()) {
                    double d3 = vec3d.distanceTo(intercept.get());
                    if (d3 < d2 || d2 == 0.0) {
                        pointedEntity = entity;
                        d2 = d3;
                    }
                }
            }
        }
        return pointedEntity;
    }
}
