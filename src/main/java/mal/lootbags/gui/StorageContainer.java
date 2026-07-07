package mal.lootbags.gui;

import mal.lootbags.item.LootbagItem;
import mal.lootbags.tileentity.TileEntityStorage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class StorageContainer extends Container {

	TileEntityStorage bench;
	private boolean flag = false;

	public StorageContainer(InventoryPlayer player, TileEntityStorage te) {
		bench = te;

		// output slot (slot 0)
		this.addSlotToContainer(new LootbagSlot(te, 0, 135, 16));

		// input slot (slot 1)
		this.addSlotToContainer(new StorageSlot(te, 1, 26, 16));

		// player inventory
		for (int i = 0; i < 3; ++i)
			for (int j = 0; j < 9; ++j)
				this.addSlotToContainer(new Slot(player, j + i * 9 + 9, 8 + j * 18, 66 + i * 18));

		// hotbar
		for (int i = 0; i < 9; ++i)
			this.addSlotToContainer(new Slot(player, i, 8 + i * 18, 123));
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return bench.isUseableByPlayer(player);
	}

	@Override
	public ItemStack slotClick(int slotId, int dragType, int clickType, EntityPlayer player) {
		flag = false;

		// Handle hotkey swap on input slot to prevent dupe
		if (clickType == 5 && slotId == 1) {
			Slot slot = (Slot) this.inventorySlots.get(slotId);
			ItemStack heldStack = player.inventory.getStackInSlot(dragType);
			ItemStack slotStack = slot.getStack();
			if (heldStack != null || slotStack != null) {
				if (heldStack == null && slot.canTakeStack(player)) {
					bench.removeBag();
				}
			}
		}

		return super.slotClick(slotId, dragType, clickType, player);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
		if (flag)
			return null;
		flag = true;

		ItemStack result = null;
		Slot slotObj = (Slot) this.inventorySlots.get(slot);

		if (slotObj != null && slotObj.getHasStack()) {
			ItemStack stack = slotObj.getStack();
			if (stack.getItem() instanceof LootbagItem && stack.stackSize > 1)
				stack.stackSize = 1;
			result = stack.copy();

			if (slot == 0) {
				// output -> player
				if (!this.mergeItemStack(stack, 2, 38, true))
					return null;
				slotObj.onSlotChange(stack, result);
			} else if (slot == 1) {
				// input -> player
				if (!this.mergeItemStack(stack, 2, 38, true))
					return null;
				bench.decrStorage(result);
				slotObj.onSlotChange(stack, result);
			} else {
				// player -> input
				if (!this.mergeItemStack(stack, 1, 2, false))
					return null;
				slotObj.onSlotChange(stack, result);
			}

			if (stack.stackSize == 0)
				slotObj.putStack(null);
			else
				slotObj.onSlotChanged();

			if (stack.stackSize == result.stackSize)
				return null;

			slotObj.onPickupFromSlot(player, stack);
		}

		return result;
	}
}
