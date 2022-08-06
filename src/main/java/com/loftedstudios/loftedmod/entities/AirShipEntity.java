package com.loftedstudios.loftedmod.entities;

import com.loftedstudios.loftedmod.blocks.LoftedBlocks;
import com.loftedstudios.loftedmod.item.AirShipItem;
import com.loftedstudios.loftedmod.item.ModItems;
import com.loftedstudios.loftedmod.mixin.client.BoatEntityAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
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
    private static final TrackedData<Integer> AIRSHIP_TYPE = DataTracker.registerData(AirShipEntity.class, TrackedDataHandlerRegistry.INTEGER);

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
    protected void initDataTracker() {
        this.dataTracker.startTracking(AIRSHIP_TYPE, AirShipEntity.Type.RED.ordinal());
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
        } else if (this.getFirstPassenger() == null) {
            this.setPitch(90);
        }
        for (int i = 0; i <= 1; ++i) {
            if (this.pressingForward || this.pressingRight || this.pressingLeft && !this.touchingWater) {
                SoundEvent soundEvents = SoundEvents.ENTITY_BOAT_PADDLE_LAND;
                if (!this.isSilent()) {
                    Vec3d vec3d = this.getRotationVec(1.0f);
                    double d = i == 1 ? -vec3d.z : vec3d.z;
                    double e = i == 1 ? vec3d.x : -vec3d.x;
                    this.world.playSound(null, this.getX() + d, this.getY(), this.getZ() + e, soundEvents, this.getSoundCategory(), 1.0f, 0.8f + 0.4f * this.random.nextFloat());
                    this.world.emitGameEvent(this.getPrimaryPassenger(), GameEvent.SPLASH, new BlockPos(this.getX() + d, this.getY(), this.getZ() + e));
                }
            }
        }
    }

    private void updateVelocity() {
        Vec3d vec3d = this.getVelocity();
        if (((BoatEntityAccessor) this).getLocation() == Location.IN_AIR || ((BoatEntityAccessor) this).getLocation() == Location.ON_LAND && this.getFirstPassenger() != null && this.pressingForward) {
            this.setVelocity(vec3d.x * 1, vec3d.y - (this.getPitch()) * 0.001, vec3d.z * 1);
        } else if (((BoatEntityAccessor) this).getLocation() == Location.IN_AIR || ((BoatEntityAccessor) this).getLocation() == Location.ON_LAND && this.getFirstPassenger() != null && !this.pressingForward) {
            this.setVelocity(vec3d.x * 1, vec3d.y - (this.getPitch()) * 0.001, vec3d.z * 1);
        }
        Vec3d velocity = this.getVelocity();
        if (!touchingWater) {
            this.setVelocity(velocity.x, velocity.y, velocity.z);
        }
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


    @Override
    public void tickRiding() {
        super.tickRiding();
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
        super.setInputs(pressingLeft, pressingRight, pressingForward, pressingBack);
        this.pressingForward = pressingForward;
        this.pressingRight = pressingRight;
        this.pressingLeft = pressingLeft;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "main", 10f, event -> {
            if (this.pressingForward || this.pressingRight && this.pressingLeft && getFirstPassenger() != null) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.airship.forward", true));
                return PlayState.CONTINUE;
            } else if (this.pressingRight && getFirstPassenger() != null) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.airship.right", true));
                return PlayState.CONTINUE;
            } else if (this.pressingLeft && getFirstPassenger() != null) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.airship.left", true));
                return PlayState.CONTINUE;
            } else {
                return PlayState.STOP;
            }
        }));
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
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
        boolean bl2 = bl = source.getAttacker() instanceof PlayerEntity && ((PlayerEntity)source.getAttacker()).getAbilities().creativeMode;
        if (bl || this.getDamageWobbleStrength() > 40.0f) {
            if (!bl && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                this.dropItem(this.asItem());
            }
            this.discard();
            playSound(SoundEvents.BLOCK_ANVIL_BREAK, 1f, 1f);

        }
        return true;
    }

    public AirShipItem asItem() {
        switch(this.getAirShipType()) {
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

    public void setAirshipType(AirShipEntity.Type type) {
        this.dataTracker.set(AIRSHIP_TYPE, type.ordinal());
    }

    public AirShipEntity.Type getAirShipType() {
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

    static{

    }

}