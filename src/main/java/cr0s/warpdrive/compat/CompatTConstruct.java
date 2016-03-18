package cr0s.warpdrive.compat;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import cr0s.warpdrive.api.IBlockTransformer;
import cr0s.warpdrive.api.ITransformation;
import cr0s.warpdrive.config.WarpDriveConfig;

public class CompatTConstruct implements IBlockTransformer {
	
	private static Class<?> classTileFurnaceLogic;
	private static Class<?> classTileFaucetLogic;
	private static Class<?> classTileSmelteryDrainLogic;
	private static Class<?> classTileSmelteryLogic;
	
	public static void register() {
		try {
			classTileFurnaceLogic = Class.forName("tconstruct.tools.logic.FurnaceLogic");
			classTileFaucetLogic = Class.forName("tconstruct.smeltery.logic.FaucetLogic");
			classTileSmelteryDrainLogic = Class.forName("tconstruct.smeltery.logic.SmelteryDrainLogic");
			classTileSmelteryLogic = Class.forName("tconstruct.smeltery.logic.SmelteryLogic");
			WarpDriveConfig.registerBlockTransformer("TinkersConstruct", new CompatTConstruct());
		} catch(ClassNotFoundException exception) {
			exception.printStackTrace();
		}
	}
	
	@Override
	public boolean isApplicable(final Block block, final int metadata, final TileEntity tileEntity) {
		return classTileFurnaceLogic.isInstance(tileEntity)
			|| classTileFaucetLogic.isInstance(tileEntity)
			|| classTileSmelteryDrainLogic.isInstance(tileEntity)
			|| classTileSmelteryLogic.isInstance(tileEntity);
	}
	
	@Override
	public boolean isJumpReady(TileEntity tileEntity) {
		return true;
	}
	
	@Override
	public NBTBase saveExternals(final TileEntity tileEntity) {
		// nothing to do
		return null;
	}
	
	@Override
	public void remove(TileEntity tileEntity) {
		// nothing to do
	}
	
	private static final byte[] mrot = {  0,  1,  5,  4,  2,  3,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15 };
	
	@Override
	public int rotate(final Block block, final int metadata, NBTTagCompound nbtTileEntity, final byte rotationSteps, final float rotationYaw) {
		if (rotationSteps == 0) {
			return metadata;
		}
		
		if (nbtTileEntity.hasKey("Direction")) {
			short direction = nbtTileEntity.getByte("Direction");
			switch (rotationSteps) {
			case 1:
				nbtTileEntity.setByte("Direction", mrot[direction]);
				return metadata;
			case 2:
				nbtTileEntity.setByte("Direction", mrot[mrot[direction]]);
				return metadata;
			case 3:
				nbtTileEntity.setByte("Direction", mrot[mrot[mrot[direction]]]);
				return metadata;
			default:
				return metadata;
			}
		}
		return metadata;
	}
	
	@Override
	public void restoreExternals(TileEntity tileEntity, ITransformation transformation, NBTBase nbtBase) {
		// nothing to do
	}
}
