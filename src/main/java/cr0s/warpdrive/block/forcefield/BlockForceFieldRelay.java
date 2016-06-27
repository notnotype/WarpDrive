package cr0s.warpdrive.block.forcefield;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.data.EnumForceFieldUpgrade;
import cr0s.warpdrive.item.ItemForceFieldUpgrade;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockForceFieldRelay extends BlockAbstractForceField {
	@SideOnly(Side.CLIENT)
	private IIcon[] icons;
	
	public BlockForceFieldRelay(final byte tier) {
		super(tier, Material.iron);
		isRotating = false;
		setBlockName("warpdrive.forcefield.relay" + tier);
		setBlockTextureName("warpdrive:forcefield/relay");
	}
	
	@Override
	public void registerBlockIcons(IIconRegister iconRegister) {
		icons = new IIcon[EnumForceFieldUpgrade.length + 1];
		
		for (EnumForceFieldUpgrade enumForceFieldUpgrade : EnumForceFieldUpgrade.values()) {
			if (enumForceFieldUpgrade.maxCountOnRelay > 0) {
				icons[enumForceFieldUpgrade.ordinal()] = iconRegister.registerIcon("warpdrive:forcefield/relay" + "_" + enumForceFieldUpgrade.unlocalizedName);
			} else {
				icons[enumForceFieldUpgrade.ordinal()] = iconRegister.registerIcon("warpdrive:forcefield/relay" + "_" + EnumForceFieldUpgrade.NONE.unlocalizedName);
			}
		}
		icons[EnumForceFieldUpgrade.length] = iconRegister.registerIcon("warpdrive:forcefield/relay_top");
	}
	
	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (tileEntity == null || !(tileEntity instanceof TileEntityForceFieldRelay)) {
			return icons[0];
		}
		
		if (side == 0 || side == 1) {
			return icons[EnumForceFieldUpgrade.length];
		}
		return icons[((TileEntityForceFieldRelay)tileEntity).getUpgrade().ordinal()];
	}
	
	@Override
	public IIcon getIcon(int side, int damage) {
		if (side == 0 || side == 1) {
			return icons[EnumForceFieldUpgrade.length];
		}
		if (damage >= 0 && damage < EnumForceFieldUpgrade.length) {
			return icons[damage];
		}
		return icons[0];
	}
	
	@Override
	public int damageDropped(int damage) {
		return damage;
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ) {
		if (world.isRemote) {
			return false;
		}
		
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (!(tileEntity instanceof TileEntityForceFieldRelay)) {
			return false;
		}
		TileEntityForceFieldRelay tileEntityForceFieldRelay = (TileEntityForceFieldRelay) tileEntity;
		ItemStack itemStackHeld = entityPlayer.getHeldItem();
		
		// sneaking with an empty hand or an upgrade item in hand to dismount current upgrade
		if (entityPlayer.isSneaking()) {
			if (tileEntityForceFieldRelay.getUpgrade() != EnumForceFieldUpgrade.NONE) {
				if (!entityPlayer.capabilities.isCreativeMode) {
					// dismount the upgrade item
					ItemStack itemStackDrop = ItemForceFieldUpgrade.getItemStackNoCache(tileEntityForceFieldRelay.getUpgrade(), 1);
					EntityItem entityItem = new EntityItem(world, entityPlayer.posX, entityPlayer.posY + 0.5D, entityPlayer.posZ, itemStackDrop);
					entityItem.delayBeforeCanPickup = 0;
					world.spawnEntityInWorld(entityItem);
				}
				
				tileEntityForceFieldRelay.setUpgrade(EnumForceFieldUpgrade.NONE);
				// upgrade dismounted
				WarpDrive.addChatMessage(entityPlayer, StatCollector.translateToLocalFormatted("warpdrive.forcefield.upgrade.result.dismounted"));
				
			} else {
				// no more upgrades to dismount
				WarpDrive.addChatMessage(entityPlayer, StatCollector.translateToLocalFormatted("warpdrive.forcefield.upgrade.result.noUpgradeToDismount"));
				return true;
			}
			
		} else if (itemStackHeld == null) {// no sneaking and no item in hand to show status
			WarpDrive.addChatMessage(entityPlayer, tileEntityForceFieldRelay.getStatus());
			return true;
			
		} else if (itemStackHeld.getItem() instanceof ItemForceFieldUpgrade) {
			// validate type
			if (EnumForceFieldUpgrade.get(itemStackHeld.getItemDamage()).maxCountOnRelay <= 0) {
				// invalid upgrade type
				WarpDrive.addChatMessage(entityPlayer, StatCollector.translateToLocalFormatted("warpdrive.forcefield.upgrade.result.invalidUpgrade"));
				return true;
			}
			
			if (!entityPlayer.capabilities.isCreativeMode) {
				// validate quantity
				if (itemStackHeld.stackSize < 1) {
					// not enough upgrade items
					WarpDrive.addChatMessage(entityPlayer, StatCollector.translateToLocalFormatted("warpdrive.forcefield.upgrade.result.notEnoughUpgrades"));
					return true;
				}
				
				// update player inventory
				itemStackHeld.stackSize -= 1;
				
				// dismount the current upgrade item
				if (tileEntityForceFieldRelay.getUpgrade() != EnumForceFieldUpgrade.NONE) {
					ItemStack itemStackDrop = ItemForceFieldUpgrade.getItemStackNoCache(tileEntityForceFieldRelay.getUpgrade(), 1);
					EntityItem entityItem = new EntityItem(world, entityPlayer.posX, entityPlayer.posY + 0.5D, entityPlayer.posZ, itemStackDrop);
					entityItem.delayBeforeCanPickup = 0;
					world.spawnEntityInWorld(entityItem);
				}
			}
			
			// mount the new upgrade item
			tileEntityForceFieldRelay.setUpgrade(EnumForceFieldUpgrade.get(itemStackHeld.getItemDamage()));
			// upgrade mounted
			WarpDrive.addChatMessage(entityPlayer, StatCollector.translateToLocalFormatted("warpdrive.forcefield.upgrade.result.mounted"));
		}
		
		return false;
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileEntityForceFieldRelay();
	}
}
