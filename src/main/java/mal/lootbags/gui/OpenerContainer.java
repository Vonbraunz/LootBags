package mal.lootbags.gui;

import mal.lootbags.tileentity.TileEntityOpener;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class OpenerContainer extends Container {

	TileEntityOpener bench;

	public OpenerContainer(InventoryPlayer player, TileEntityOpener te) {
		bench = te;

		// input (9 bag slots)
		for (int i = 0; i < 9; i++)
			this.addSlotToContainer(new OpenerLootbagSlot(te, i, 8 + i * 18, 19));

		// output (27 item slots)
		for (int j = 0; j < 3; j++)
			for (int i = 0; i < 9; i++)
				this.addSlotToContainer(new LootbagSlot(te, 9 + i + 9 * j, 8 + i * 18, 43 + j * 18));

		// player inventory
		for (int i = 0; i < 3; ++i)
			for (int j = 0; j < 9; ++j)
				this.addSlotToContainer(new Slot(player, j + i * 9 + 9, 8 + j * 18, 102 + i * 18));

		// hotbar
		for (int i = 0; i < 9; ++i)
			this.addSlotToContainer(new Slot(player, i, 8 + i * 18, 159));
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return bench.isUseableByPlayer(player);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
		ItemStack result = null;
		Slot slotObj = (Slot) this.inventorySlots.get(slot);

		if (slotObj != null && slotObj.getHasStack()) {
			ItemStack stack = slotObj.getStack();
			result = stack.copy();

			if (slot >= 0 && slot <= 35) {
				// TE slots -> player inventory
				if (!this.mergeItemStack(stack, 36, 72, true))
					return null;
				slotObj.onSlotChange(stack, result);
			} else {
				// player inventory -> input slots
				if (!this.mergeItemStack(stack, 0, 9, false))
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
