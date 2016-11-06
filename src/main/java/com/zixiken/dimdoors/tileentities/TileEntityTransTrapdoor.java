package com.zixiken.dimdoors.tileentities;

import java.util.Random;

public class TileEntityTransTrapdoor extends DDTileEntityBase {

	@Override
	public float[] getRenderColor(Random rand) {
		float[] rgbaColor = {1,1,1,1};
		if (this.worldObj.provider.getDimension() == -1) {
			rgbaColor[0] = worldObj.rand.nextFloat() * 0.5F + 0.4F;
			rgbaColor[1] = worldObj.rand.nextFloat() * 0.05F;
			rgbaColor[2] = worldObj.rand.nextFloat() * 0.05F;
		} else {
			rgbaColor[0] = worldObj.rand.nextFloat() * 0.5F + 0.1F;
			rgbaColor[1] = worldObj.rand.nextFloat() * 0.4F + 0.4F;
			rgbaColor[2] = worldObj.rand.nextFloat() * 0.6F + 0.5F;
		}
		return rgbaColor;
	}
}
