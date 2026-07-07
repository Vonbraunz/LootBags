package mal.lootbags.network;

import mal.lootbags.LootBags;
import mal.lootbags.gui.LootbagContainer;
import mal.lootbags.gui.OpenerContainer;
import mal.lootbags.gui.RecyclerContainer;
import mal.lootbags.gui.StorageContainer;
import mal.lootbags.tileentity.TileEntityOpener;
import mal.lootbags.tileentity.TileEntityRecycler;
import mal.lootbags.tileentity.TileEntityStorage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
//import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class CommonProxy implements IGuiHandler{

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		if(ID==0 && player.getCurrentEquippedItem() != null && Item.getIdFromItem(player.getCurrentEquippedItem().getItem()) == Item.getIdFromItem(LootBags.lootbagItem))
		{
			return new LootbagContainer(player.inventory, new LootbagWrapper(player.getCurrentEquippedItem(), player.inventory.currentItem));
		}
		if(ID==1)
			return new RecyclerContainer(player.inventory, (TileEntityRecycler) world.getTileEntity(x, y, z));
		if(ID==2)
			return new OpenerContainer(player.inventory, (TileEntityOpener) world.getTileEntity(x, y, z));
		if(ID==3)
			return new StorageContainer(player.inventory, (TileEntityStorage) world.getTileEntity(x, y, z));
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		return null;
	}

    public World getClientWorld()
    {
        return null;
    }

	public void registerRenderers() {}
}
/*******************************************************************************
 * Copyright (c) 2016 Malorolam.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the included license.
 * 
 *********************************************************************************/