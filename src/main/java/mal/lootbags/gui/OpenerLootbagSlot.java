package mal.lootbags.gui;

import mal.lootbags.item.LootbagItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class OpenerLootbagSlot extends Slot {

	public OpenerLootbagSlot(IInventory inventory, int index, int xPosition, int yPosition) {
		super(inventory, index, xPosition, yPosition);
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		if (stack == null)
			return false;
		return stack.getItem() instanceof LootbagItem;
	}
}
