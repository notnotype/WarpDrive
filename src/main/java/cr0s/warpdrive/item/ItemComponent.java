package cr0s.warpdrive.item;

import java.util.List;

import cr0s.warpdrive.data.EnumComponentType;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import cr0s.warpdrive.WarpDrive;
import cr0s.warpdrive.api.IAirCanister;
import net.minecraft.util.text.translation.I18n;

public class ItemComponent extends Item implements IAirCanister {	
	private IIcon[] icons;
	private static ItemStack[] itemStackCache;
	
	public ItemComponent() {
		super();
		setHasSubtypes(true);
		setUnlocalizedName("warpdrive.crafting.component");
		setCreativeTab(WarpDrive.creativeTabWarpDrive);
		
		icons = new IIcon[EnumComponentType.length];
		itemStackCache = new ItemStack[EnumComponentType.length];
	}
	
	public static ItemStack getItemStack(EnumComponentType enumComponentType) {
		if (enumComponentType != null) {
			int damage = enumComponentType.ordinal();
			if (itemStackCache[damage] == null) {
				itemStackCache[damage] = new ItemStack(WarpDrive.itemComponent, 1, damage);
			}
			return itemStackCache[damage];
		}
		return null;
	}
	
	public static ItemStack getItemStackNoCache(EnumComponentType enumComponentType, int amount) {
		return new ItemStack(WarpDrive.itemComponent, amount, enumComponentType.ordinal());
	}
	
	@Override
	public void registerIcons(IIconRegister par1IconRegister) {
		for(EnumComponentType enumComponentType : EnumComponentType.values()) {
			icons[enumComponentType.ordinal()] = par1IconRegister.registerIcon("warpdrive:component/" + enumComponentType.unlocalizedName);
		}
	}
	
	@Override
	public String getUnlocalizedName(ItemStack itemStack) {
		int damage = itemStack.getItemDamage();
		if (damage >= 0 && damage < EnumComponentType.length) {
			return "item.warpdrive.crafting." + EnumComponentType.get(damage).unlocalizedName;
		}
		return getUnlocalizedName();
	}
	
	@Override
	public IIcon getIconFromDamage(int damage) {
		if (damage >= 0 && damage < EnumComponentType.length) {
			return icons[damage];
		}
		return icons[0];
	}
	
	@Override
	public void getSubItems(Item item, CreativeTabs creativeTab, List list) {
		for(EnumComponentType enumComponentType : EnumComponentType.values()) {
			list.add(new ItemStack(item, 1, enumComponentType.ordinal()));
		}
	}
	
	// For empty air canister
	@Override
	public boolean canContainAir(ItemStack itemStack) {
		return (itemStack.getItem() instanceof ItemComponent && itemStack.getItemDamage() == EnumComponentType.AIR_CANISTER.ordinal());
	}
	
	@Override
	public boolean containsAir(ItemStack itemStack) {
		return false;
	}
	
	@Override
	public ItemStack fullDrop(ItemStack itemStack) {
		if (canContainAir(itemStack)) {
			return WarpDrive.itemAirCanisterFull.fullDrop(itemStack);
		}
		return null;
	}
	
	@Override
	public ItemStack emptyDrop(ItemStack itemStack) {
		if (canContainAir(itemStack)) {
			return WarpDrive.itemAirCanisterFull.emptyDrop(itemStack);
		}
		return null;
	}
	
	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean advancedItemTooltips) {
		super.addInformation(itemStack, entityPlayer, list, advancedItemTooltips);
		
		String tooltip = "";
		switch (EnumComponentType.get(itemStack.getItemDamage())) {
		case AIR_CANISTER:
			tooltip += I18n.translateToLocalFormatted("item.warpdrive.crafting.AirCanisterEmpty.tooltip");
			break;
		default:
			break;
		}
		
		WarpDrive.addTooltip(list, tooltip);
	}
}