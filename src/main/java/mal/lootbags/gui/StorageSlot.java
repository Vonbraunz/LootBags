package mal.lootbags.gui;

import mal.lootbags.LootBags;
import mal.lootbags.handler.BagHandler;
import mal.lootbags.item.LootbagItem;
import mal.lootbags.tileentity.TileEntityStorage;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class StorageSlot extends Slot {

	public StorageSlot(IInventory inventory, int index, int xPosition, int yPosition) {
		super(inventory, index, xPosition, yPosition);
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		if (!(stack.getItem() instanceof LootbagItem))
			return false;
		if (LootBags.PREVENTMERGEDBAGS) {
			if (!BagHandler.isBagOpened(stack) && BagHandler.isBagInsertable(stack.getItemDamage())) {
				int stored = ((TileEntityStorage) this.inventory).getStorage();
				int val = BagHandler.getBagValue(stack.getItemDamage())[0];
				if (stored + val >= Integer.MAX_VALUE || stored + val < 0)
					return false;
				return true;
			}
		} else {
			if (BagHandler.isBagInsertable(stack.getItemDamage())) {
				int stored = ((TileEntityStorage) this.inventory).getStorage();
				int val = BagHandler.getBagValue(stack.getItemDamage())[0];
				if (stored + val >= Integer.MAX_VALUE || stored + val < 0)
					return false;
				return true;
			}
		}
		return false;
	}
}
