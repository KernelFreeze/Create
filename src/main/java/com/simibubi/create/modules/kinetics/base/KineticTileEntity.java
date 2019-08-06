package com.simibubi.create.modules.kinetics.base;

import java.util.Optional;
import java.util.Random;

import com.simibubi.create.foundation.block.SyncedTileEntity;
import com.simibubi.create.modules.kinetics.RotationPropagator;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public abstract class KineticTileEntity extends SyncedTileEntity {

	protected float speed;
	protected float force;
	protected Optional<BlockPos> source;

	public KineticTileEntity(TileEntityType<?> typeIn) {
		super(typeIn);
		setSpeed(0);
		setForce(0);
		source = Optional.empty();
	}
	
	@Override
	public void onLoad() {
		if (!hasWorld())
			return;
		super.onLoad();
	}

	@Override
	public void remove() {
		if (world.isRemote) {
			super.remove();
			return;
		}
		RotationPropagator.handleRemoved(getWorld(), getPos(), this);
		super.remove();
	}

	public void notifyBlockUpdate() {
		this.world.notifyBlockUpdate(getPos(), getBlockState(), getBlockState(), 2 | 16);
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.putFloat("Speed", getSpeed());
		compound.putFloat("Force", getForce());

		if (hasSource())
			compound.put("Source", NBTUtil.writeBlockPos(getSource()));

		return super.write(compound);
	}

	@Override
	public void read(CompoundNBT compound) {
		setSpeed(compound.getFloat("Speed"));
		setForce(compound.getFloat("Force"));

		setSource(null);
		if (compound.contains("Source")) {
			CompoundNBT tagSource = compound.getCompound("Source");
			setSource(NBTUtil.readBlockPos(tagSource));
		}

		super.read(compound);
	}

	public boolean isSource() {
		return false;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {

//		if (hasWorld())
//			Minecraft.getInstance().player.sendStatusMessage(
//					new StringTextComponent((getWorld().isRemote ? TextFormatting.RED : TextFormatting.GREEN)
//							+ "" + getClass().getSimpleName() + getPos().toString() + " to " + speed),
//					false);

		this.speed = speed;
		if (hasWorld() && speed != 0) {
			Random r = getWorld().rand;
			for (int i = 0; i < 10; i++) {
				float x = getPos().getX() + (r.nextFloat() - .5f) / 2f + .5f;
				float y = getPos().getY() + (r.nextFloat() - .5f) / 2f + .5f;
				float z = getPos().getZ() + (r.nextFloat() - .5f) / 2f + .5f;
				this.getWorld().addParticle(new RedstoneParticleData(1, 1, 1, 1), x, y, z, 0, 0, 0);
			}
		}
	}

	public float getForce() {
		return force;
	}

	public void setForce(float force) {
		this.force = force;
	}

	public boolean hasSource() {
		return source.isPresent();
	}

	public BlockPos getSource() {
		return source.get();
	}

	public Direction getSourceFacing() {
		BlockPos source = getSource().subtract(getPos());
		return Direction.getFacingFromVector(source.getX(), source.getY(), source.getZ());
	}

	public void setSource(BlockPos source) {
		this.source = Optional.ofNullable(source);
	}

	public void removeSource() {
		this.source = Optional.empty();
		setSpeed(0);
	}

}
