package com.loftedstudios.loftedmod.item;

import com.loftedstudios.loftedmod.entities.AirShipEntity;
import com.loftedstudios.loftedmod.entities.ModEntityTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class AirShipItem extends Item {
    private static final Predicate<Entity> RIDERS = EntityPredicates.EXCEPT_SPECTATOR.and(Entity::collides);
    private final AirShipEntity.Type type;

    public AirShipItem(AirShipEntity.Type type, Settings settings) {
        super(settings);
        this.type = type;
    }


    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        HitResult hitResult = raycast(world, user, RaycastContext.FluidHandling.ANY);
        AirShipEntity airShipEntity = new AirShipEntity(ModEntityTypes.AIRSHIP, world);


        if (hitResult.getType() == HitResult.Type.MISS) {
            return TypedActionResult.pass(itemStack);
        } else {
            Vec3d vec3d = user.getRotationVec(1.0F);
            double d = 5.0D;
            List<Entity> list = world.getOtherEntities(user, user.getBoundingBox().stretch(vec3d.multiply(5.0D)).expand(1.0D), RIDERS);
            if (!list.isEmpty()) {
                Vec3d vec3d2 = user.getEyePos();
                Iterator var11 = list.iterator();

                while (var11.hasNext()) {
                    Entity entity = (Entity) var11.next();
                    Box box = entity.getBoundingBox().expand(entity.getTargetingMargin());
                    if (box.contains(vec3d2)) {
                        return TypedActionResult.pass(itemStack);
                    }
                }
            }

            if (hitResult.getType() == HitResult.Type.BLOCK) {
                BlockPos finalPos = new BlockPos(hitResult.getPos().x, hitResult.getPos().y + 1, hitResult.getPos().z);
                airShipEntity.setPos(finalPos.getX(), finalPos.getY(), finalPos.getZ());
                airShipEntity.setAirshipType(this.type);
                airShipEntity.setYaw(user.getYaw());
                if (!world.isClient) {
                    world.spawnEntity(airShipEntity);
                    world.emitGameEvent(user, GameEvent.ENTITY_PLACE, new BlockPos(hitResult.getPos()));
                    if (!user.getAbilities().creativeMode) {
                        itemStack.decrement(1);
                    }
                }

                user.incrementStat(Stats.USED.getOrCreateStat(this));
                return TypedActionResult.success(itemStack, world.isClient());
            } else {
                return TypedActionResult.pass(itemStack);
            }
        }
    }
}
