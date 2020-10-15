package br.nom.yt.re.lilcreeper.entity;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.CompositeEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class LilCreeperModel<T extends Entity> extends CompositeEntityModel<T> {
	private final ModelPart head;
	private final ModelPart helmet;
	private final ModelPart torso;
	private final ModelPart rightBackLeg;
	private final ModelPart leftBackLeg;
	private final ModelPart rightFrontLeg;
	private final ModelPart leftFrontLeg;

	public LilCreeperModel() {
		this(0.0F);
	}

	public LilCreeperModel(float scale) {
		this.head = new ModelPart(this, 0, 0);
		this.head.addCuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, scale);
		this.head.setPivot(0.0F, 6.0F +6, 0.0F);
		this.helmet = new ModelPart(this, 32, 0);
		this.helmet.addCuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, scale + 0.5F);
		this.helmet.setPivot(0.0F, 6.0F +6, 0.0F);
		this.torso = new ModelPart(this, 16, 16);
		this.torso.addCuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F -6, 4.0F, scale);
		this.torso.setPivot(0.0F, 6.0F + 6, 0.0F);
		this.rightBackLeg = new ModelPart(this, 0, 16);
		this.rightBackLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, scale);
		this.rightBackLeg.setPivot(-2.0F, 18.0F, 4.0F);
		this.leftBackLeg = new ModelPart(this, 0, 16);
		this.leftBackLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, scale);
		this.leftBackLeg.setPivot(2.0F, 18.0F, 4.0F);
		this.rightFrontLeg = new ModelPart(this, 0, 16);
		this.rightFrontLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, scale);
		this.rightFrontLeg.setPivot(-2.0F, 18.0F, -4.0F);
		this.leftFrontLeg = new ModelPart(this, 0, 16);
		this.leftFrontLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, scale);
		this.leftFrontLeg.setPivot(2.0F, 18.0F, -4.0F);
	}

	@Override
	public Iterable<ModelPart> getParts() {
		return ImmutableList.of(this.head, this.torso, this.rightBackLeg, this.leftBackLeg, this.rightFrontLeg, this.leftFrontLeg);
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		this.head.yaw = headYaw * 0.017453292F;
		this.head.pitch = headPitch * 0.017453292F;
		this.rightBackLeg.pitch = MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance;
		this.leftBackLeg.pitch = MathHelper.cos(limbAngle * 0.6662F + 3.1415927F) * 1.4F * limbDistance;
		this.rightFrontLeg.pitch = MathHelper.cos(limbAngle * 0.6662F + 3.1415927F) * 1.4F * limbDistance;
		this.leftFrontLeg.pitch = MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance;
	}
}
