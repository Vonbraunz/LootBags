package mal.lootbags.tileentity;

import java.util.ArrayList;

import mal.lootbags.LootBags;
import mal.lootbags.handler.BagHandler;
import mal.lootbags.item.LootbagItem;
import mal.lootbags.network.LootbagsPacketHandler;
import mal.lootbags.network.message.StorageMessageClient;
import mal.lootbags.network.message.StorageMessageServer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.NetworkRegistry;

public class TileEntityStorage extends TileEntity implements IInventory, ISidedInventory {

	private int stored_value;
	private int outputID, outputindex;
	private ItemStack input_inventory;
	private ArrayList<Integer> outputIDlist;
	private boolean justRemoved = false;
	private NetworkRegistry.TargetPoint point;

	public TileEntityStorage() {
		input_inventory = null;
		outputIDlist = new ArrayList<Integer>();
		outputIDlist.addAll(BagHandler.getExtractedBagList());
		outputindex = 0;
		outputID = outputIDlist.get(outputindex);
	}

	public void activate(World world, int x, int y, int z, EntityPlayer player) {
		player.openGui(LootBags.LootBagsInstance, 3, world, x, y, z);
	}

	public void updateEntity() {
		if (worldObj != null && !worldObj.isRemote) {
			if (point == null)
				point = new NetworkRegistry.TargetPoint(worldObj.provider.dimensionId, xCoord, yCoord, zCoord, 16);
			LootbagsPacketHandler.instance.sendToAllAround(new StorageMessageServer(this, stored_value, outputID, outputindex), point);
		}
		justRemoved = false;
	}

	public void setDataClient(int value, int ID, int index) {
		stored_value = value;
		outputID = ID;
		outputindex = index;
	}

	public void setDataServer(int ID, int index) {
		outputID = ID;
		outputindex = index;
		markDirty();
	}

	public void cycleOutputID(boolean direction) {
		if (direction) {
			outputindex++;
			if (outputindex == outputIDlist.size())
				outputindex = 0;
		} else {
			outputindex--;
			if (outputindex < 0)
				outputindex = outputIDlist.size() - 1;
		}
		try {
			outputID = outputIDlist.get(outputindex);
		} catch (IndexOutOfBoundsException e) {
			outputID = outputIDlist.get(0);
			outputindex = 0;
		}
		if (worldObj.isRemote)
			LootbagsPacketHandler.instance.sendToServer(new StorageMessageClient(this, outputID, outputindex));
	}

	public int getStorage() { return stored_value; }

	public ItemStack getOutputStack() {
		return new ItemStack(LootBags.lootbagItem, 1, outputID);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		outputID = nbt.getInteger("outputID");
		stored_value = nbt.getInteger("totalValue");
		outputindex = nbt.getInteger("outputindex");

		NBTTagList input = nbt.getTagList("inputItems", 10);
		if (input.tagCount() > 0)
			input_inventory = ItemStack.loadItemStackFromNBT(input.getCompoundTagAt(0));
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("outputID", outputID);
		nbt.setInteger("totalValue", stored_value);
		nbt.setInteger("outputindex", outputindex);

		NBTTagList input = new NBTTagList();
		NBTTagCompound var4 = new NBTTagCompound();
		if (input_inventory != null)
			input_inventory.writeToNBT(var4);
		input.appendTag(var4);
		nbt.setTag("inputItems", input);
	}

	public NBTTagCompound getDropNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("outputID", outputID);
		tag.setInteger("stored_value", stored_value);
		tag.setInteger("outputindex", outputindex);
		return tag;
	}

	public boolean removeBag() {
		int value = BagHandler.getBagValue(outputID)[1];
		if (stored_value >= value) {
			stored_value -= value;
			justRemoved = true;
			return true;
		}
		return false;
	}

	public int getID() { return outputID; }

	public void decrStorage(ItemStack is) {
		if (is != null && is.getItem() instanceof LootbagItem) {
			int value = BagHandler.getBagValue(is.getItemDamage())[1];
			if (stored_value >= value)
				stored_value -= value;
			else
				stored_value = 0;
		}
	}

	@Override
	public String getInventoryName() { return "storage"; }

	@Override
	public boolean hasCustomInventoryName() { return false; }

	@Override
	public int[] getAccessibleSlotsFromSide(int side) { return new int[]{0, 1}; }

	@Override
	public boolean canInsertItem(int index, ItemStack stack, int side) {
		if (!(stack.getItem() instanceof LootbagItem))
			return false;
		if (LootBags.PREVENTMERGEDBAGS) {
			if (!BagHandler.isBagOpened(stack) && BagHandler.isBagInsertable(stack.getItemDamage())) {
				if (stored_value + BagHandler.getBagValue(stack.getItemDamage())[0] == Integer.MAX_VALUE
						|| stored_value + BagHandler.getBagValue(stack.getItemDamage())[0] < 0)
					return false;
				return true;
			}
		} else {
			if (BagHandler.isBagInsertable(stack.getItemDamage())) {
				if (stored_value + BagHandler.getBagValue(stack.getItemDamage())[0] == Integer.MAX_VALUE
						|| stored_value + BagHandler.getBagValue(stack.getItemDamage())[0] < 0)
					return false;
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, int side) { return true; }

	@Override
	public int getSizeInventory() { return 2; }

	@Override
	public ItemStack getStackInSlot(int index) {
		if (index == 0) {
			if (stored_value >= BagHandler.getBagValue(outputID)[1] || justRemoved) {
				if (LootBags.STOREDCOUNT) {
					if (LootBags.MEKOVERRIDE) {
						String ss = (new Throwable()).getStackTrace()[1].getClassName();
						if (ss.startsWith("mekanism"))
							return new ItemStack(LootBags.lootbagItem, 1, outputID);
					}
					return new ItemStack(LootBags.lootbagItem, (int) Math.floor(stored_value / BagHandler.getBagValue(outputID)[1]), outputID);
				} else
					return new ItemStack(LootBags.lootbagItem, 1, outputID);
			} else
				return null;
		} else
			return input_inventory;
	}

	@Override
	public ItemStack decrStackSize(int slot, int dec) {
		if (slot == 0) {
			int value = BagHandler.getBagValue(outputID)[1];
			if (stored_value >= value) {
				stored_value -= value;
				return new ItemStack(LootBags.lootbagItem, 1, outputID);
			}
		} else {
			if (input_inventory != null) {
				ItemStack is;
				if (input_inventory.stackSize <= dec) {
					is = input_inventory;
					input_inventory = null;
					return is;
				} else {
					is = input_inventory.splitStack(dec);
					if (input_inventory.stackSize == 0)
						input_inventory = null;
					return is;
				}
			}
		}
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int index) { return null; }

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		if (stack == null || !(stack.getItem() instanceof LootbagItem)) {
			if (stored_value < BagHandler.getBagValue(outputID)[1])
				return;
			else {
				stored_value -= BagHandler.getBagValue(outputID)[1];
				return;
			}
		}
		int value = BagHandler.getBagValue(stack)[0];
		if (value < 1)
			return;
		stored_value += value;
		markDirty();
	}

	@Override
	public int getInventoryStackLimit() { return 1; }

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return worldObj.getTileEntity(xCoord, yCoord, zCoord) != this ? false
				: player.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) <= 64.0;
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		if (!(stack.getItem() instanceof LootbagItem))
			return false;
		if (LootBags.PREVENTMERGEDBAGS) {
			if (!BagHandler.isBagOpened(stack) && BagHandler.isBagInsertable(stack.getItemDamage())) {
				if (stored_value + BagHandler.getBagValue(stack.getItemDamage())[0] >= Integer.MAX_VALUE
						|| stored_value + BagHandler.getBagValue(stack.getItemDamage())[0] < 0)
					return false;
				return true;
			}
		} else {
			if (BagHandler.isBagInsertable(stack.getItemDamage())) {
				if (stored_value + BagHandler.getBagValue(stack.getItemDamage())[0] >= Integer.MAX_VALUE
						|| stored_value + BagHandler.getBagValue(stack.getItemDamage())[0] < 0)
					return false;
				return true;
			}
		}
		return false;
	}
}
