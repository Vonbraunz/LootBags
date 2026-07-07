package mal.lootbags.network;

import mal.lootbags.LootBags;
import mal.lootbags.gui.LootbagGui;
import mal.lootbags.gui.OpenerGui;
import mal.lootbags.gui.RecyclerGui;
import mal.lootbags.gui.StorageGui;
import mal.lootbags.tileentity.TileEntityOpener;
import mal.lootbags.tileentity.TileEntityRecycler;
import mal.lootbags.tileentity.TileEntityStorage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
//import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import cpw.mods.fml.client.FMLClientHandler;

public class ClientProxy extends CommonProxy{

	@Override
    public World getClientWorld()
    {
        return FMLClientHandler.instance().getClient().theWorld;
    }
	
	@Override
	public void registerRenderers()
	{
		//ItemRenderingRegister.registerItemRender();
	}
	
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		if(ID==0 && player.getCurrentEquippedItem() != null && Item.getIdFromItem(player.getCurrentEquippedItem().getItem()) == Item.getIdFromItem(LootBags.lootbagItem))
		{
			return new LootbagGui(player.inventory, new LootbagWrapper(player.getCurrentEquippedItem(), player.inventory.currentItem));
		}
		if(ID==1)
			return new RecyclerGui(player.inventory, (TileEntityRecycler) world.getTileEntity(x, y, z));
		if(ID==2)
			return new OpenerGui(player.inventory, (TileEntityOpener) world.getTileEntity(x, y, z));
		if(ID==3)
			return new StorageGui(player.inventory, (TileEntityStorage) world.getTileEntity(x, y, z));
		return null;
	}
}
/*******************************************************************************
 * Copyright (c) 2016 Malorolam.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the included license.
 * 
 *********************************************************************************/