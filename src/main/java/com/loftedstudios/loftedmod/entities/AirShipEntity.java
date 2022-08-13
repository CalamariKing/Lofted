package com.loftedstudios.loftedmod.entities;

import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import com.loftedstudios.loftedmod.item.AirShipItem;
import com.loftedstudios.loftedmod.item.ModItems;
import com.loftedstudios.loftedmod.sound.ModSounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LilyPadBlock;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.BoatPaddleStateC2SPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;

// I basically had to recreate the BoatEntity class to access the ability to create variations. it's messy but it works

public class AirShipEntity extends Entity implements IAnimatable {
    private static final TrackedData<Integer> DAMAGE_WOBBLE_TICKS = DataTracker.registerData(AirShipEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> DAMAGE_WOBBLE_SIDE = DataTracker.registerData(AirShipEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Float> DAMAGE_WOBBLE_STRENGTH = DataTracker.registerData(AirShipEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Integer> AIRSHIP_TYPE = DataTracker.registerData(AirShipEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> LEFT_PADDLE_MOVING = DataTracker.registerData(AirShipEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> RIGHT_PADDLE_MOVING = DataTracker.registerData(AirShipEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> BUBBLE_WOBBLE_TICKS = DataTracker.registerData(AirShipEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private float velocityDecay;
    private float ticksUnderwater;
    private float yawVelocity;
    private int field_7708;
    private double x;
    private double y;
    private double z;
    private double boatYaw;
    private double boatPitch;
    private boolean pressingLeft;
    private boolean pressingRight;
    private boolean pressingForward;
    private boolean pressingBack;
    private double waterLevel;
    private float field_7714;
    private Location location;
    private Location lastLocation;
    private double fallVelocity;
    private boolean onBubbleColumnSurface;
    private boolean bubbleColumnIsDrag;
    private AnimationFactory factory = new AnimationFactory(this);

    public AirShipEntity(EntityType<? extends AirShipEntity> entityType, World world) {
        super(entityType, world);
        this.stepHeight = 1.0F;
    }

    @Override
    protected float getEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return dimensions.height;
    }

    @Override
    protected Entity.MoveEffect getMoveEffect() {
        return Entity.MoveEffect.NONE;
    }


    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    protected void initDataTracker() {
        this.dataTracker.startTracking(DAMAGE_WOBBLE_TICKS, 0);
        this.dataTracker.startTracking(DAMAGE_WOBBLE_SIDE, 1);
        this.dataTracker.startTracking(DAMAGE_WOBBLE_STRENGTH, 0.0F);
        this.dataTracker.startTracking(AIRSHIP_TYPE, AirShipEntity.Type.RED.ordinal());
        this.dataTracker.startTracking(LEFT_PADDLE_MOVING, false);
        this.dataTracker.startTracking(RIGHT_PADDLE_MOVING, false);
        this.dataTracker.startTracking(BUBBLE_WOBBLE_TICKS, 0);
    }

    public boolean collidesWith(Entity other) {
        return canCollide(this, other);
    }

    public static boolean canCollide(Entity entity, Entity other) {
        return (other.isCollidable() || other.isPushable()) && !entity.isConnectedThroughVehicle(other);
    }

    public boolean isCollidable() {
        return true;
    }

    public boolean isPushable() {
        return true;
    }
    public void setInputs(boolean pressingLeft, boolean pressingRight, boolean pressingForward, boolean pressingBack) {
        this.pressingLeft = pressingLeft;
        this.pressingRight = pressingRight;
        this.pressingForward = pressingForward;
        this.pressingBack = pressingBack;
    }

    protected Vec3d positionInPortal(Direction.Axis portalAxis, BlockLocating.Rectangle portalRect) {
        return LivingEntity.positionInPortal(super.positionInPortal(portalAxis, portalRect));
    }


    public double getMountedHeightOffset() {
        return -0.1D;
    }

    public void onBubbleColumnSurfaceCollision(boolean drag) {
        if (!this.world.isClient) {
            this.onBubbleColumnSurface = true;
            this.bubbleColumnIsDrag = drag;
            if (this.getBubbleWobbleTicks() == 0) {
                this.setBubbleWobbleTicks(60);
            }
        }

        this.world.addParticle(ParticleTypes.SPLASH, this.getX() + (double) this.random.nextFloat(), this.getY() + 0.7D, this.getZ() + (double) this.random.nextFloat(), 0.0D, 0.0D, 0.0D);
        if (this.random.nextInt(20) == 0) {
            this.world.playSound(this.getX(), this.getY(), this.getZ(), this.getSplashSound(), this.getSoundCategory(), 1.0F, 0.8F + 0.4F * this.random.nextFloat(), false);
        }

        this.emitGameEvent(GameEvent.SPLASH, this.getPrimaryPassenger());
    }

    public void pushAwayFrom(Entity entity) {
        if (entity instanceof AirShipEntity) {
            if (entity.getBoundingBox().minY < this.getBoundingBox().maxY) {
                super.pushAwayFrom(entity);
            }
        } else if (entity.getBoundingBox().minY <= this.getBoundingBox().minY) {
            super.pushAwayFrom(entity);
        }

    }

    public void animateDamage() {
        this.setDamageWobbleSide(-this.getDamageWobbleSide());
        this.setDamageWobbleTicks(10);
        this.setDamageWobbleStrength(this.getDamageWobbleStrength() * 11.0F);
    }

    public boolean collides() {
        return !this.isRemoved();
    }

    public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.boatYaw = (double) yaw;
        this.boatPitch = (double) pitch;
        this.field_7708 = 10;
    }


    public Direction getMovementDirection() {
        return this.getHorizontalFacing().rotateYClockwise();
    }
// its messy but it works so yea
    @Override
    public void tick() {
        this.lastLocation = this.location;
        this.location = this.checkLocation();
        if (this.location != AirShipEntity.Location.UNDER_WATER && this.location != AirShipEntity.Location.UNDER_FLOWING_WATER) {
            this.ticksUnderwater = 0.0F;
        } else {
            ++this.ticksUnderwater;
        }

        if (!this.world.isClient && this.ticksUnderwater >= 60.0F) {
            this.removeAllPassengers();
        }

        if (this.getDamageWobbleTicks() > 0) {
            this.setDamageWobbleTicks(this.getDamageWobbleTicks() - 1);
        }

        if (this.getDamageWobbleStrength() > 0.0F) {
            this.setDamageWobbleStrength(this.getDamageWobbleStrength() - 1.0F);
        }
        super.tick();
        this.method_7555();
        if (this.isLogicalSideForUpdatingMovement()) {
            if (!(this.getFirstPassenger() instanceof PlayerEntity)) {
                this.setPaddleMovings(false, false);
            }

            this.updateVelocity();
            if (this.world.isClient) {
                this.updatePaddles();
                this.world.sendPacket(new BoatPaddleStateC2SPacket(this.isPaddleMoving(0), this.isPaddleMoving(1)));
            }

            this.move(MovementType.SELF, this.getVelocity());
        } else {
            this.setVelocity(Vec3d.ZERO);
        }

        if(random.nextInt(200) > 197 && this.getFirstPassenger() != null){
            playSound(ModSounds.BLIMP_CREAK, 0.8f, 1f);
        }
        if(random.nextInt(10) > 8 && this.getFirstPassenger() != null && this.isMoving()){
            playSound(SoundEvents.ENTITY_BOAT_PADDLE_LAND, 0.8f, 1f);
        }
        updateYawVelocity();
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

    private void updateYawVelocity() {
        Vec3d vec3d = this.getVelocity();
        if (this.location == Location.IN_AIR || this.location == Location.ON_LAND && this.getFirstPassenger() != null && this.pressingForward) {
            this.setVelocity(vec3d.x * 1, vec3d.y - (this.getPitch()) * 0.001, vec3d.z * 1);
        } else if (this.location == Location.IN_AIR || this.location == Location.ON_LAND && this.getFirstPassenger() != null && !this.pressingForward) {
            this.setVelocity(vec3d.x * 1, vec3d.y - (this.getPitch()) * 0.001, vec3d.z * 1);
        }
        Vec3d velocity = this.getVelocity();
        if (!touchingWater) {
            this.setVelocity(velocity.x, velocity.y, velocity.z);
        }
    }

    private void updateVelocity() {
        double d = -0.03999999910593033D;
        double e = this.hasNoGravity() ? 0.0D : -0.03999999910593033D;
        double f = 0.0D;
        this.velocityDecay = 0.05F;
        if (this.lastLocation == AirShipEntity.Location.IN_AIR && this.location != AirShipEntity.Location.IN_AIR && this.location != AirShipEntity.Location.ON_LAND) {
            this.waterLevel = this.getBodyY(1.0D);
            this.setPosition(this.getX(), (double)(this.method_7544() - this.getHeight()) + 0.101D, this.getZ());
            this.setVelocity(this.getVelocity().multiply(1.0D, 0.0D, 1.0D));
            this.fallVelocity = 0.0D;
            this.location = AirShipEntity.Location.IN_WATER;
        } else {
            if (this.location == AirShipEntity.Location.IN_WATER) {
                f = (this.waterLevel - this.getY()) / (double)this.getHeight();
                this.velocityDecay = 0.9F;
            } else if (this.location == AirShipEntity.Location.UNDER_FLOWING_WATER) {
                e = -7.0E-4D;
                this.velocityDecay = 0.9F;
            } else if (this.location == AirShipEntity.Location.UNDER_WATER) {
                f = 0.009999999776482582D;
                this.velocityDecay = 0.45F;
            } else if (this.location == AirShipEntity.Location.IN_AIR) {
                this.velocityDecay = 0.9F;
            } else if (this.location == AirShipEntity.Location.ON_LAND) {
                this.velocityDecay = this.field_7714;
                if (this.getPrimaryPassenger() instanceof PlayerEntity) {
                    this.field_7714 /= 2.0F;
                }
            }

            Vec3d vec3d = this.getVelocity();
            this.setVelocity(vec3d.x * (double)this.velocityDecay, vec3d.y + e, vec3d.z * (double)this.velocityDecay);
            this.yawVelocity *= this.velocityDecay;
            if (f > 0.0D) {
                Vec3d vec3d2 = this.getVelocity();
                this.setVelocity(vec3d2.x, (vec3d2.y + f * 0.06153846016296973D) * 0.75D, vec3d2.z);
            }
        }

    }

    private boolean isMoving(){
        return this.pressingForward || this.pressingRight || this.pressingLeft;
    }

    private void updatePaddles() {
        if (this.hasPassengers()) {
            float f = 0.0F;
            if (this.pressingLeft) {
                --this.yawVelocity;
            }

            if (this.pressingRight) {
                ++this.yawVelocity;
            }

            if (this.pressingRight != this.pressingLeft && !this.pressingForward && !this.pressingBack) {
                f += 0.005F;
            }

            this.setYaw(this.getYaw() + this.yawVelocity);
            if (this.pressingForward) {
                f += 0.04F;
            }

            if (this.pressingBack) {
                f -= 0.005F;
            }

            this.setVelocity(this.getVelocity().add((double)(MathHelper.sin(-this.getYaw() * 0.017453292F) * f), 0.0D, (double)(MathHelper.cos(this.getYaw() * 0.017453292F) * f)));
            this.setPaddleMovings(this.pressingRight && !this.pressingLeft || this.pressingForward, this.pressingLeft && !this.pressingRight || this.pressingForward);
        }
    }
    @Override
    public void updatePassengerPosition(Entity passenger) {
        super.updatePassengerPosition(passenger);
    }

    private void method_7555() {
        if (this.isLogicalSideForUpdatingMovement()) {
            this.field_7708 = 0;
            this.updateTrackedPosition(this.getX(), this.getY(), this.getZ());
        }

        if (this.field_7708 > 0) {
            double d = this.getX() + (this.x - this.getX()) / (double)this.field_7708;
            double e = this.getY() + (this.y - this.getY()) / (double)this.field_7708;
            double f = this.getZ() + (this.z - this.getZ()) / (double)this.field_7708;
            double g = MathHelper.wrapDegrees(this.boatYaw - (double)this.getYaw());
            this.setYaw(this.getYaw() + (float)g / (float)this.field_7708);
            this.setPitch(this.getPitch() + (float)(this.boatPitch - (double)this.getPitch()) / (float)this.field_7708);
            --this.field_7708;
            this.setPosition(d, e, f);
            this.setRotation(this.getYaw(), this.getPitch());
        }
    }




    public void setPaddleMovings(boolean leftMoving, boolean rightMoving) {
        this.dataTracker.set(LEFT_PADDLE_MOVING, leftMoving);
        this.dataTracker.set(RIGHT_PADDLE_MOVING, rightMoving);
    }


    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putString("Type", this.getAirShipType().getName());
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.contains("Type", 8)) {
            this.setAirshipType(AirShipEntity.Type.getType(nbt.getString("Type")));
        }
    }
// returns the location of the entity
    private AirShipEntity.Location checkLocation() {
        AirShipEntity.Location location = this.getUnderWaterLocation();
        if (location != null) {
            this.waterLevel = this.getBoundingBox().maxY;
            return location;
        } else if (this.checkBoatInWater()) {
            return AirShipEntity.Location.IN_WATER;
        } else {
            float f = this.method_7548();
            if (f > 0.0F) {
                this.field_7714 = f;
                return AirShipEntity.Location.ON_LAND;
            } else {
                return AirShipEntity.Location.IN_AIR;
            }
        }
    }

    public float method_7544() {
        Box box = this.getBoundingBox();
        int i = MathHelper.floor(box.minX);
        int j = MathHelper.ceil(box.maxX);
        int k = MathHelper.floor(box.maxY);
        int l = MathHelper.ceil(box.maxY - this.fallVelocity);
        int m = MathHelper.floor(box.minZ);
        int n = MathHelper.ceil(box.maxZ);
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        label39:
        for (int o = k; o < l; ++o) {
            float f = 0.0F;

            for (int p = i; p < j; ++p) {
                for (int q = m; q < n; ++q) {
                    mutable.set(p, o, q);
                    FluidState fluidState = this.world.getFluidState(mutable);
                    if (fluidState.isIn(FluidTags.WATER)) {
                        f = Math.max(f, fluidState.getHeight(this.world, mutable));
                    }

                    if (f >= 1.0F) {
                        continue label39;
                    }
                }
            }

            if (f < 1.0F) {
                return (float) mutable.getY() + f;
            }
        }

        return (float) (l + 1);
    }

    public float method_7548() {
        Box box = this.getBoundingBox();
        Box box2 = new Box(box.minX, box.minY - 0.001D, box.minZ, box.maxX, box.minY, box.maxZ);
        int i = MathHelper.floor(box2.minX) - 1;
        int j = MathHelper.ceil(box2.maxX) + 1;
        int k = MathHelper.floor(box2.minY) - 1;
        int l = MathHelper.ceil(box2.maxY) + 1;
        int m = MathHelper.floor(box2.minZ) - 1;
        int n = MathHelper.ceil(box2.maxZ) + 1;
        VoxelShape voxelShape = VoxelShapes.cuboid(box2);
        float f = 0.0F;
        int o = 0;
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (int p = i; p < j; ++p) {
            for (int q = m; q < n; ++q) {
                int r = (p != i && p != j - 1 ? 0 : 1) + (q != m && q != n - 1 ? 0 : 1);
                if (r != 2) {
                    for (int s = k; s < l; ++s) {
                        if (r <= 0 || s != k && s != l - 1) {
                            mutable.set(p, s, q);
                            BlockState blockState = this.world.getBlockState(mutable);
                            if (!(blockState.getBlock() instanceof LilyPadBlock) && VoxelShapes.matchesAnywhere(blockState.getCollisionShape(this.world, mutable).offset((double) p, (double) s, (double) q), voxelShape, BooleanBiFunction.AND)) {
                                f += blockState.getBlock().getSlipperiness();
                                ++o;
                            }
                        }
                    }
                }
            }
        }
        return f / (float) o;
    }

    private boolean checkBoatInWater() {
        Box box = this.getBoundingBox();
        int i = MathHelper.floor(box.minX);
        int j = MathHelper.ceil(box.maxX);
        int k = MathHelper.floor(box.minY);
        int l = MathHelper.ceil(box.minY + 0.001D);
        int m = MathHelper.floor(box.minZ);
        int n = MathHelper.ceil(box.maxZ);
        boolean bl = false;
        this.waterLevel = -1.7976931348623157E308D;
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (int o = i; o < j; ++o) {
            for (int p = k; p < l; ++p) {
                for (int q = m; q < n; ++q) {
                    mutable.set(o, p, q);
                    FluidState fluidState = this.world.getFluidState(mutable);
                    if (fluidState.isIn(FluidTags.WATER)) {
                        float f = (float) p + fluidState.getHeight(this.world, mutable);
                        this.waterLevel = Math.max((double) f, this.waterLevel);
                        bl |= box.minY < (double) f;
                    }
                }
            }
        }
        return bl;
    }

    @Override
    public Vec3d updatePassengerForDismount(LivingEntity passenger) {
        Vec3d vec3d = getPassengerDismountOffset((double) (this.getWidth() * MathHelper.SQUARE_ROOT_OF_TWO), (double) passenger.getWidth(), passenger.getYaw());
        double d = this.getX() + vec3d.x;
        double e = this.getZ() + vec3d.z;
        BlockPos blockPos = new BlockPos(d, this.getBoundingBox().maxY, e);
        BlockPos blockPos2 = blockPos.down();
        if (!this.world.isWater(blockPos2)) {
            List<Vec3d> list = Lists.newArrayList();
            double f = this.world.getDismountHeight(blockPos);
            if (Dismounting.canDismountInBlock(f)) {
                list.add(new Vec3d(d, (double) blockPos.getY() + f, e));
            }

            double g = this.world.getDismountHeight(blockPos2);
            if (Dismounting.canDismountInBlock(g)) {
                list.add(new Vec3d(d, (double) blockPos2.getY() + g, e));
            }

            UnmodifiableIterator var14 = passenger.getPoses().iterator();

            while (var14.hasNext()) {
                EntityPose entityPose = (EntityPose) var14.next();
                Iterator var16 = list.iterator();

                while (var16.hasNext()) {
                    Vec3d vec3d2 = (Vec3d) var16.next();
                    if (Dismounting.canPlaceEntityAt(this.world, vec3d2, passenger, entityPose)) {
                        passenger.setPose(entityPose);
                        return vec3d2;
                    }
                }
            }
        }

        return super.updatePassengerForDismount(passenger);
    }


    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        return player.startRiding(this) ? ActionResult.CONSUME : ActionResult.PASS;
    }

    @Override
    protected void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition) {
        this.fallVelocity = this.getVelocity().y;
        if (!this.hasVehicle()) {
            if (onGround) {
                if (this.fallDistance > 3.0F) {
                    if (this.location != AirShipEntity.Location.ON_LAND) {
                        this.fallDistance = 0.0F;
                        return;
                    }

                    this.handleFallDamage(this.fallDistance, 1.0F, DamageSource.FALL);
                    if (!this.world.isClient && !this.isRemoved()) {
                        this.kill();
                        if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                            int j;
                            for (j = 0; j < 3; ++j) {
                                this.dropItem(this.getAirShipType().getBaseBlock());
                            }

                            for (j = 0; j < 2; ++j) {
                                this.dropItem(Items.STICK);
                            }
                        }
                    }
                }

                this.fallDistance = 0.0F;
            } else if (!this.world.getFluidState(this.getBlockPos().down()).isIn(FluidTags.WATER) && heightDifference < 0.0D) {
                this.fallDistance = (float) ((double) this.fallDistance - heightDifference);
            }

        }
    }

    public boolean isPaddleMoving(int paddle) {
        return (Boolean) this.dataTracker.get(paddle == 0 ? LEFT_PADDLE_MOVING : RIGHT_PADDLE_MOVING) && this.getPrimaryPassenger() != null;
    }

    public void setDamageWobbleStrength(float wobbleStrength) {
        this.dataTracker.set(DAMAGE_WOBBLE_STRENGTH, wobbleStrength);
    }

    public float getDamageWobbleStrength() {
        return (Float) this.dataTracker.get(DAMAGE_WOBBLE_STRENGTH);
    }

    public void setDamageWobbleTicks(int wobbleTicks) {
        this.dataTracker.set(DAMAGE_WOBBLE_TICKS, wobbleTicks);
    }

    public int getDamageWobbleTicks() {
        return (Integer) this.dataTracker.get(DAMAGE_WOBBLE_TICKS);
    }

    private void setBubbleWobbleTicks(int wobbleTicks) {
        this.dataTracker.set(BUBBLE_WOBBLE_TICKS, wobbleTicks);
    }

    private int getBubbleWobbleTicks() {
        return (Integer) this.dataTracker.get(BUBBLE_WOBBLE_TICKS);
    }

    public void setDamageWobbleSide(int side) {
        this.dataTracker.set(DAMAGE_WOBBLE_SIDE, side);
    }

    public int getDamageWobbleSide() {
        return (Integer) this.dataTracker.get(DAMAGE_WOBBLE_SIDE);
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return this.getPassengerList().size() < 2;
    }

    @Nullable
    @Override
    public Entity getPrimaryPassenger() {
        return this.getFirstPassenger();
    }
    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }

    @Override
    public boolean isSubmergedInWater() {
        return this.location == AirShipEntity.Location.UNDER_WATER || this.location == AirShipEntity.Location.UNDER_FLOWING_WATER;
    }

    @Override
    public ItemStack getPickBlockStack() {
        return new ItemStack(this.asItem());
    }

    @Override
    public boolean hasNoGravity() {
        return true;
    }


    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_PHANTOM_SWOOP, 0.15F, 1.0F);
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "main", 10f, event -> {
            if (this.hasPassengers() && this.pressingForward || this.pressingRight && this.pressingLeft && getFirstPassenger() != null) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.airship.forward", true));
                return PlayState.CONTINUE;
            } else if (this.hasPassengers() && this.pressingRight && getFirstPassenger() != null) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.airship.right", true));
                return PlayState.CONTINUE;
            } else if (this.hasPassengers() && this.pressingLeft && getFirstPassenger() != null) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.airship.left", true));
                return PlayState.CONTINUE;
            } else {
                return PlayState.STOP;
            }
        }));
    }

    @Nullable
    private AirShipEntity.Location getUnderWaterLocation() {
        Box box = this.getBoundingBox();
        double d = box.maxY + 0.001D;
        int i = MathHelper.floor(box.minX);
        int j = MathHelper.ceil(box.maxX);
        int k = MathHelper.floor(box.maxY);
        int l = MathHelper.ceil(d);
        int m = MathHelper.floor(box.minZ);
        int n = MathHelper.ceil(box.maxZ);
        boolean bl = false;
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (int o = i; o < j; ++o) {
            for (int p = k; p < l; ++p) {
                for (int q = m; q < n; ++q) {
                    mutable.set(o, p, q);
                    FluidState fluidState = this.world.getFluidState(mutable);
                    if (fluidState.isIn(FluidTags.WATER) && d < (double) ((float) mutable.getY() + fluidState.getHeight(this.world, mutable))) {
                        if (!fluidState.isStill()) {
                            return Location.UNDER_FLOWING_WATER;
                        }

                        bl = true;
                    }
                }
            }
        }
        return bl ? AirShipEntity.Location.UNDER_WATER : null;
    }

        @Override
        public boolean damage (DamageSource source,float amount){
            boolean bl;
            if (this.isInvulnerableTo(source)) {
                return false;
            }
            if (this.world.isClient || this.isRemoved()) {
                return true;
            }
            this.setDamageWobbleSide(-this.getDamageWobbleSide());
            this.setDamageWobbleTicks(10);
            this.setDamageWobbleStrength(this.getDamageWobbleStrength() + amount * 10.0f);
            this.scheduleVelocityUpdate();
            this.emitGameEvent(GameEvent.ENTITY_DAMAGED, source.getAttacker());
            boolean bl2 = bl = source.getAttacker() instanceof PlayerEntity && ((PlayerEntity) source.getAttacker()).getAbilities().creativeMode;
            if (bl || this.getDamageWobbleStrength() > 40.0f) {
                if (!bl && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                    this.dropItem(this.asItem());
                }
                this.discard();
                playSound(ModSounds.BLIMP_DESTROY, 1f, 1f);


            }
            return true;
        }

        public AirShipItem asItem () {
            switch (this.getAirShipType()) {
                case RED:
                default:
                    return ModItems.RED_AIRSHIP;
                case YELLOW:
                    return ModItems.YELLOW_AIRSHIP;
                case BLACK:
                    return ModItems.BLACK_AIRSHIP;
                case BLUE:
                    return ModItems.BLUE_AIRSHIP;
            }
        }

        public void setAirshipType (AirShipEntity.Type type){
            this.dataTracker.set(AIRSHIP_TYPE, type.ordinal());
        }

        public AirShipEntity.Type getAirShipType () {
            return AirShipEntity.Type.getType(this.dataTracker.get(AIRSHIP_TYPE));
        }

        public static enum Type {
            RED(Blocks.RED_WOOL, "red"),
            YELLOW(Blocks.YELLOW_WOOL, "yellow"),
            BLUE(Blocks.BLUE_WOOL, "blue"),
            BLACK(Blocks.BLACK_WOOL, "black");

            private final String name;
            private final Block baseBlock;

            Type(Block baseBlock, String name) {
                this.name = name;
                this.baseBlock = baseBlock;
            }

            public String getName() {
                return this.name;
            }

            public Block getBaseBlock() {
                return this.baseBlock;
            }

            public String toString() {
                return this.name;
            }

            public static AirShipEntity.Type getType(int type) {
                AirShipEntity.Type[] types = AirShipEntity.Type.values();
                if (type < 0 || type >= types.length) {
                    type = 0;
                }
                return types[type];
            }

            public static AirShipEntity.Type getType(String name) {
                AirShipEntity.Type[] types = AirShipEntity.Type.values();
                for (int i = 0; i < types.length; ++i) {
                    if (!types[i].getName().equals(name)) continue;
                    return types[i];
                }
                return types[0];
            }
        }
        public static enum Location {
            IN_WATER,
            UNDER_WATER,
            UNDER_FLOWING_WATER,
            ON_LAND,
            IN_AIR;
        }

}