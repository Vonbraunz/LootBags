package mal.lootbags.blocks;

import mal.lootbags.LootBags;
import mal.lootbags.tileentity.TileEntityStorage;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;

public class BlockStorage extends BlockContainer {

	private final String name = "loot_storage";

	public BlockStorage() {
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
		return new TileEntityStorage();
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (world.isRemote)
			return true;
		TileEntity te = world.getTileEntity(x, y, z);
		if (!(te instanceof TileEntityStorage))
			return false;
		((TileEntityStorage) te).activate(world, x, y, z, player);
		return true;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, net.minecraft.block.Block block, int meta) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityStorage) {
			NBTTagCompound dropTag = ((TileEntityStorage) te).getDropNBT();
			ItemStack drop = new ItemStack(this, 1, 0);
			drop.setTagCompound(dropTag);
			float rx = world.rand.nextFloat() * 0.8f + 0.1f;
			float ry = world.rand.nextFloat() * 0.8f + 0.1f;
			float rz = world.rand.nextFloat() * 0.8f + 0.1f;
			net.minecraft.entity.item.EntityItem entity = new net.minecraft.entity.item.EntityItem(
					world, x + rx, y + ry, z + rz, drop);
			world.spawnEntityInWorld(entity);
		}
		super.breakBlock(world, x, y, z, block, meta);
	}

	@Override
	public void registerBlockIcons(IIconRegister ir) {
		this.blockIcon = ir.registerIcon("lootbags:storageTexture");
	}
}
