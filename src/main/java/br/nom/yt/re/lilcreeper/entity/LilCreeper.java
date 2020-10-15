package br.nom.yt.re.lilcreeper.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.EnumSet;

@SuppressWarnings("EntityConstructor")
public class LilCreeper extends HostileEntity {
	private static final TrackedData<Integer> FUSE_SPEED = DataTracker.registerData(LilCreeper.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Boolean> IGNITED = DataTracker.registerData(LilCreeper.class, TrackedDataHandlerRegistry.BOOLEAN);
	private int lastFuseTime;
	private int currentFuseTime;
	private int fuseTime = 30;
	private int explosionRadius = 3;

	public LilCreeper(EntityType<? extends HostileEntity> entityType, World world) {
		super(entityType, world);
	}

	public static DefaultAttributeContainer.Builder createLilCreeperAttributes() {
		return HostileEntity.createHostileAttributes()
				.add(EntityAttributes.GENERIC_MAX_HEALTH, 0.5)
				.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25D);
	}

	@Override
	protected int getCurrentExperience(PlayerEntity player) {
		return 0;
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(1, new SwimGoal(this));
		this.goalSelector.add(2, new LilCreeperIgniteGoal(this));
		this.goalSelector.add(3, new FleeEntityGoal<>(this, OcelotEntity.class, 6.0F, 1.0D, 1.2D));
		this.goalSelector.add(3, new FleeEntityGoal<>(this, CatEntity.class, 6.0F, 1.0D, 1.2D));
		this.goalSelector.add(4, new MeleeAttackGoal(this, 1.0D, false));
		this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.8D));
		this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goalSelector.add(6, new LookAroundGoal(this));
		this.targetSelector.add(1, new FollowTargetGoal<>(this, PlayerEntity.class, true));
		this.targetSelector.add(2, new RevengeGoal(this));
	}

	@Override
	public int getSafeFallDistance() {
		return this.getTarget() == null ? 3 : 3 + (int) (this.getHealth() - 1.0F);
	}

	@Override
	public boolean handleFallDamage(float fallDistance, float damageMultiplier) {
		boolean bl = super.handleFallDamage(fallDistance, damageMultiplier);
		this.currentFuseTime = (int) ((float) this.currentFuseTime + fallDistance * 1.5F);
		if (this.currentFuseTime > this.fuseTime - 5) {
			this.currentFuseTime = this.fuseTime - 5;
		}

		return bl;
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(FUSE_SPEED, -1);
		this.dataTracker.startTracking(IGNITED, false);
	}

	public void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);

		tag.putShort("Fuse", (short) this.fuseTime);
		tag.putByte("ExplosionRadius", (byte) this.explosionRadius);
		tag.putBoolean("ignited", this.getIgnited());
	}

	public void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		if (tag.contains("Fuse", 99)) {
			this.fuseTime = tag.getShort("Fuse");
		}

		if (tag.contains("ExplosionRadius", 99)) {
			this.explosionRadius = tag.getByte("ExplosionRadius");
		}

		if (tag.getBoolean("ignited")) {
			this.ignite();
		}
	}

	@Override
	public void tick() {
		if (this.isAlive()) {
			this.lastFuseTime = this.currentFuseTime;
			if (this.getIgnited()) {
				this.setFuseSpeed(1);
			}

			int i = this.getFuseSpeed();
			if (i > 0 && this.currentFuseTime == 0) {
				this.playSound(SoundEvents.ENTITY_CREEPER_PRIMED, 1.0F, 0.5F);
			}

			this.currentFuseTime += i;
			if (this.currentFuseTime < 0) {
				this.currentFuseTime = 0;
			}

			if (this.currentFuseTime >= this.fuseTime) {
				this.currentFuseTime = this.fuseTime;
				this.explode();
				this.currentFuseTime = 0;
			}
		}

		super.tick();
	}

	private void explode() {
		if (!this.world.isClient) {
			this.jump();
			this.spawnEffectsCloud();
		}
	}

	private void spawnEffectsCloud() {
		Collection<StatusEffectInstance> collection = this.getStatusEffects();
		if (!collection.isEmpty()) {
			AreaEffectCloudEntity areaEffectCloudEntity = new AreaEffectCloudEntity(this.world, this.getX(), this.getY(), this.getZ());
			areaEffectCloudEntity.setRadius(2.5F);
			areaEffectCloudEntity.setRadiusOnUse(-0.5F);
			areaEffectCloudEntity.setWaitTime(10);
			areaEffectCloudEntity.setDuration(areaEffectCloudEntity.getDuration() / 2);
			areaEffectCloudEntity.setRadiusGrowth(-areaEffectCloudEntity.getRadius() / (float) areaEffectCloudEntity.getDuration());

			for (StatusEffectInstance statusEffectInstance : collection) {
				areaEffectCloudEntity.addEffect(new StatusEffectInstance(statusEffectInstance));
			}

			this.world.spawnEntity(areaEffectCloudEntity);
		}

	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_CREEPER_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_CREEPER_DEATH;
	}

	public boolean tryAttack(Entity target) {
		return true;
	}

	public int getFuseTime() {
		return fuseTime;
	}

	public int getFuseSpeed() {
		return this.dataTracker.get(FUSE_SPEED);
	}

	public void setFuseSpeed(int fuseSpeed) {
		this.dataTracker.set(FUSE_SPEED, fuseSpeed);
	}

	public boolean getIgnited() {
		return this.dataTracker.get(IGNITED);
	}

	public void ignite() {
		this.dataTracker.set(IGNITED, true);
	}

	@Environment(EnvType.CLIENT)
	public float getClientFuseTime(float timeDelta) {
		return MathHelper.lerp(timeDelta, (float)this.lastFuseTime, (float)this.currentFuseTime) / (float)(this.fuseTime - 2);
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		return super.damage(source, amount);
	}

	private class LilCreeperIgniteGoal extends Goal {
		private final LilCreeper creeper;
		private LivingEntity target;

		public LilCreeperIgniteGoal(LilCreeper creeper) {
			this.creeper = creeper;
			this.setControls(EnumSet.of(Control.MOVE));
		}

		public boolean canStart() {
			LivingEntity livingEntity = this.creeper.getTarget();
			return this.creeper.getFuseSpeed() > 0 || livingEntity != null && this.creeper.squaredDistanceTo(livingEntity) < 9.0D;
		}

		public void start() {
			this.creeper.getNavigation().stop();
			this.target = this.creeper.getTarget();
		}

		public void stop() {
			this.target = null;
		}

		public void tick() {
			if (this.target == null) {
				this.creeper.setFuseSpeed(-1);
			} else if (this.creeper.squaredDistanceTo(this.target) > 49.0D) {
				this.creeper.setFuseSpeed(-1);
			} else if (!this.creeper.getVisibilityCache().canSee(this.target)) {
				this.creeper.setFuseSpeed(-1);
			} else {
				this.creeper.setFuseSpeed(1);
			}
		}
	}
}
