package mal.lootbags.blocks;

import mal.lootbags.LootBags;
import mal.lootbags.tileentity.TileEntityOpener;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;

public class BlockOpener extends BlockContainer {

	private final String name = "loot_opener";

	public BlockOpener() {
		super(Material.rock);
		GameRegistry.registerBlock(this, name);
		this.setBlockName(LootBags.MODID + "_" + name);
		this.setHardness(1.5f);
		this.setResistance(20f);
		this.setCreativeTab(CreativeTabs.tabBlock);
	}

	public String getName() { return name; }

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityOpener();
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (world.isRemote)
			return true;
		TileEntity te = world.getTileEntity(x, y, z);
		if (!(te instanceof TileEntityOpener))
			return false;
		((TileEntityOpener) te).activate(world, x, y, z, player);
		return true;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, net.minecraft.block.Block block, int meta) {
		TileEntityOpener te = (TileEntityOpener) world.getTileEntity(x, y, z);
		if (te != null) {
			for (int i = 0; i < te.getSizeInventory(); i++) {
				ItemStack stack = te.getStackInSlot(i);
				if (stack != null) {
					float rx = world.rand.nextFloat() * 0.8f + 0.1f;
					float ry = world.rand.nextFloat() * 0.8f + 0.1f;
					float rz = world.rand.nextFloat() * 0.8f + 0.1f;
					net.minecraft.entity.item.EntityItem entity = new net.minecraft.entity.item.EntityItem(
							world, x + rx, y + ry, z + rz, stack.copy());
					world.spawnEntityInWorld(entity);
				}
			}
		}
		super.breakBlock(world, x, y, z, block, meta);
	}

	@Override
	public void registerBlockIcons(IIconRegister ir) {
		this.blockIcon = ir.registerIcon("lootbags:openerTexture");
	}
}
