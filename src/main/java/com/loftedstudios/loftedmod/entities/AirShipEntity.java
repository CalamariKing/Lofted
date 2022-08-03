package com.loftedstudios.loftedmod.entities;

import com.loftedstudios.loftedmod.mixin.client.BoatEntityAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class AirShipEntity extends BoatEntity implements IAnimatable {
    public boolean pressingForward;
    public boolean pressingRight;
    public boolean pressingLeft;
    private AnimationFactory factory = new AnimationFactory(this);
    public AirShipEntity(EntityType<? extends BoatEntity> entityType, World world) {
        super(entityType, world);
        this.stepHeight = 1.0F;
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @Override
    public void tick() {
        super.tick();
        updateVelocity();
        this.move(MovementType.SELF, this.getVelocity());
        if (this.getFirstPassenger() != null && this.pressingForward) {
            Entity passenger = this.getFirstPassenger();
            this.setPitch(passenger.getPitch() * 0.5F);
        }
        if (this.getFirstPassenger() != null && !this.pressingForward) {
            Entity passenger = this.getFirstPassenger();
            this.setPitch(0);
        }
    }

    private void updateVelocity() {
        Vec3d vec3d = this.getVelocity();
        if (((BoatEntityAccessor) this).getLocation() == Location.IN_AIR || ((BoatEntityAccessor) this).getLocation() == Location.ON_LAND && this.getFirstPassenger() != null && this.pressingForward) {
            this.setVelocity(vec3d.x * 1, vec3d.y - (this.getPitch()) * 0.001, vec3d.z * 1);
        }
        else if (((BoatEntityAccessor) this).getLocation() == Location.IN_AIR || ((BoatEntityAccessor) this).getLocation() == Location.ON_LAND && this.getFirstPassenger() != null && !this.pressingForward) {
            this.setVelocity(vec3d.x * 1, vec3d.y - (this.getPitch()) * 0.001, vec3d.z * 1);
        }
        Vec3d velocity = this.getVelocity();
        if (!touchingWater) {
            this.setVelocity(velocity.x, velocity.y, velocity.z);
        } else if(!touchingWater && this.getFirstPassenger() == null){
            this.setVelocity(velocity.x * 0.4, -0.75, velocity.z * 0.4);
        }

    }

    @Override
    public boolean hasNoGravity() {
        return true;
    }
    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        return player.startRiding(this) ? ActionResult.CONSUME : ActionResult.PASS;
    }
    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return this.getPassengerList().size() < 2;
    }
    @Override
    public void updatePassengerPosition(Entity passenger) {
        super.updatePassengerPosition(passenger);
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_PHANTOM_SWOOP, 0.15F, 1.0F);
    }
    public void setInputs(boolean pressingLeft, boolean pressingRight, boolean pressingForward, boolean pressingBack) {
        super.setInputs(pressingLeft,pressingRight,pressingForward,pressingBack);
        this.pressingForward = pressingForward;
        this.pressingRight = pressingRight;
        this.pressingLeft = pressingLeft;
    }

    @Override
    public void registerControllers(AnimationData data) {
        if(getFirstPassenger() != null) {
            data.addAnimationController(new AnimationController<>(this, "main", 10f, event -> {
                if (this.pressingForward || this.pressingRight && this.pressingLeft) {
                    event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.airship.forward", true));
                    return PlayState.CONTINUE;
                } else if (this.pressingRight) {
                    event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.airship.right", true));
                    return PlayState.CONTINUE;
                } else if (this.pressingLeft) {
                    event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.airship.left", true));
                    return PlayState.CONTINUE;
                } else {
                    return PlayState.STOP;
                }
            }));
        }
    }
}