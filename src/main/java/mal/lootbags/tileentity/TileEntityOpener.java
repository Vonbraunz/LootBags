package mal.lootbags.tileentity;

import mal.lootbags.LootBags;
import mal.lootbags.item.LootbagItem;
import mal.lootbags.network.LootbagsPacketHandler;
import mal.lootbags.network.message.OpenerMessageServer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.NetworkRegistry;

public class TileEntityOpener extends TileEntity implements IInventory, ISidedInventory {

	private ItemStack[] lootbagInventory = new ItemStack[9];
	private int cooldown = 0;
	private ItemStack[] inventory = new ItemStack[27];
	private NetworkRegistry.TargetPoint point;

	public TileEntityOpener() {
		for (int i = 0; i < lootbagInventory.length; i++)
			lootbagInventory[i] = null;
		for (int i = 0; i < inventory.length; i++)
			inventory[i] = null;
	}

	@Override
	public String getInventoryName() {
		return "opener";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	public void updateEntity() {
		if (worldObj != null && !worldObj.isRemote) {
			if (point == null)
				point = new NetworkRegistry.TargetPoint(worldObj.provider.dimensionId, xCoord, yCoord, zCoord, 16);

			if (cooldown > 0)
				cooldown--;
			else {
				boolean opened = false;
				int invslot = 0;
				while (!opened && invslot < lootbagInventory.length) {
					ItemStack stack = lootbagInventory[invslot];
					if (stack == null)
						invslot++;
					else if (!(stack.getItem() instanceof LootbagItem)) {
						insertItemToOutput(stack);
						invslot++;
					} else {
						LootbagItem.generateInventory(stack);
						ItemStack[] llist = LootbagItem.getInventory(stack);
						for (int i = 0; i < llist.length; i++) {
							llist[i] = insertItemToOutput(llist[i]);
						}
						LootbagItem.setTagCompound(stack, llist);
						if (LootbagItem.checkInventory(stack)) {
							cooldown = LootBags.OPENERMAXCOOLDOWN;
							setInventorySlotContents(invslot, null);
							if (LootBags.OPENERMAXCOOLDOWN > 0)
								opened = true;
						} else
							invslot++;
					}
				}
				LootbagsPacketHandler.instance.sendToAllAround(new OpenerMessageServer(this, cooldown), point);
			}
		} else if (worldObj != null && worldObj.isRemote) {
			if (cooldown > 0)
				cooldown--;
		}
	}

	private ItemStack insertItemToOutput(ItemStack is) {
		if (is == null)
			return null;
		for (int i = 0; i < inventory.length; i++) {
			if (inventory[i] == null) {
				inventory[i] = is.copy();
				return null;
			} else if (LootBags.areItemStacksEqualItem(is, inventory[i], true, true)) {
				if (inventory[i].stackSize < inventory[i].getMaxStackSize()) {
					if (inventory[i].stackSize + is.stackSize <= inventory[i].getMaxStackSize()) {
						inventory[i].stackSize += is.stackSize;
						return null;
					} else {
						int diff = inventory[i].stackSize + is.stackSize - inventory[i].getMaxStackSize();
						inventory[i].stackSize = inventory[i].getMaxStackSize();
						is.stackSize = diff;
						return is.copy();
					}
				}
			}
		}
		return is;
	}

	public void activate(World world, int x, int y, int z, EntityPlayer player) {
		player.openGui(LootBags.LootBagsInstance, 2, world, x, y, z);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		cooldown = nbt.getInteger("cooldown");

		NBTTagList input = nbt.getTagList("inputItems", 10);
		for (int i = 0; i < input.tagCount(); ++i) {
			NBTTagCompound var4 = input.getCompoundTagAt(i);
			byte var5 = var4.getByte("Slot");
			if (var5 >= 0 && var5 < lootbagInventory.length)
				lootbagInventory[var5] = ItemStack.loadItemStackFromNBT(var4);
		}

		NBTTagList output = nbt.getTagList("outputItems", 10);
		for (int i = 0; i < output.tagCount(); ++i) {
			NBTTagCompound var4 = output.getCompoundTagAt(i);
			byte var5 = var4.getByte("Slot");
			if (var5 >= 0 && var5 < inventory.length)
				inventory[var5] = ItemStack.loadItemStackFromNBT(var4);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("cooldown", cooldown);

		NBTTagList input = new NBTTagList();
		for (int i = 0; i < lootbagInventory.length; ++i) {
			if (lootbagInventory[i] != null) {
				NBTTagCompound var4 = new NBTTagCompound();
				var4.setByte("Slot", (byte) i);
				lootbagInventory[i].writeToNBT(var4);
				input.appendTag(var4);
			}
		}
		nbt.setTag("inputItems", input);

		NBTTagList output = new NBTTagList();
		for (int i = 0; i < inventory.length; ++i) {
			if (inventory[i] != null) {
				NBTTagCompound var4 = new NBTTagCompound();
				var4.setByte("Slot", (byte) i);
				inventory[i].writeToNBT(var4);
				output.appendTag(var4);
			}
		}
		nbt.setTag("outputItems", output);
	}

	public int getCooldown() { return cooldown; }

	public void setData(int cd) { cooldown = cd; }

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		int[] slots = new int[getSizeInventory()];
		for (int i = 0; i < slots.length; i++)
			slots[i] = i;
		return slots;
	}

	@Override
	public boolean canInsertItem(int index, ItemStack stack, int side) {
		return stack.getItem() instanceof LootbagItem && index >= 0 && index < lootbagInventory.length;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, int side) {
		return index >= lootbagInventory.length && index < getSizeInventory();
	}

	@Override
	public int getSizeInventory() { return lootbagInventory.length + inventory.length; }

	@Override
	public ItemStack getStackInSlot(int index) {
		if (index >= 0 && index < lootbagInventory.length)
			return lootbagInventory[index];
		else if (index < getSizeInventory())
			return inventory[index - lootbagInventory.length];
		return null;
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		ItemStack stack = getStackInSlot(index);
		if (stack != null) {
			ItemStack result;
			if (stack.stackSize <= count) {
				result = stack;
				setInventorySlotContents(index, null);
				markDirty();
				return result;
			} else {
				result = stack.splitStack(count);
				if (stack.stackSize <= 0)
					setInventorySlotContents(index, null);
				else
					setInventorySlotContents(index, stack);
				markDirty();
				return result;
			}
		}
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int index) { return null; }

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		if (stack != null && stack.stackSize > getInventoryStackLimit())
			stack.stackSize = getInventoryStackLimit();
		if (stack != null && stack.stackSize <= 0)
			stack = null;

		if (index >= 0 && index < lootbagInventory.length)
			lootbagInventory[index] = stack;
		else if (index < getSizeInventory())
			inventory[index - lootbagInventory.length] = stack;

		markDirty();
	}

	@Override
	public int getInventoryStackLimit() { return 64; }

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
		return index >= 0 && index < lootbagInventory.length
				&& stack.getItem() instanceof LootbagItem && stack.stackSize == 1;
	}
}
