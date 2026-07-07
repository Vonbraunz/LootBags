package mal.lootbags.gui;

import org.lwjgl.opengl.GL11;

import mal.lootbags.LootBags;
import mal.lootbags.LootbagsUtil;
import mal.lootbags.handler.BagHandler;
import mal.lootbags.tileentity.TileEntityStorage;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class StorageGui extends GuiContainer {

	private TileEntityStorage bench;

	public StorageGui(InventoryPlayer player, TileEntityStorage te) {
		super(new StorageContainer(player, te));
		bench = te;
		ySize = 186;
	}

	@Override
	public void initGui() {
		super.initGui();
		this.buttonList.add(new GuiButton(1, this.width / 2 - 27, this.height / 2 - 42, 54, 12, "Cycle Bag"));
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button.id == 1)
			bench.cycleOutputID(true);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);

		if (LootbagsUtil.isPointInRegion(135, 16, 16, 16, mouseX, mouseY, guiLeft, guiTop))
			this.renderToolTip(new ItemStack(LootBags.lootbagItem, 1, bench.getID()), mouseX, mouseY);

		if (LootbagsUtil.isPointInRegion(44, 26, 40, 8, mouseX, mouseY, guiLeft, guiTop))
			this.drawCreativeTabHoveringText(Integer.toString(bench.getStorage()), mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(new ResourceLocation("lootbags", "textures/gui/storage_gui.png"));
		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		this.fontRendererObj.drawString("Lootbag Storage", 52, 5, 4210752);
		this.fontRendererObj.drawString("Stored: ", 44, 16, 4210752);
		this.fontRendererObj.drawString(LootbagsUtil.formatSciNot(bench.getStorage()), 44, 26, 4210752);
		this.fontRendererObj.drawString("Needed: ", 96, 16, 4210752);
		this.fontRendererObj.drawString(LootbagsUtil.formatSciNot(BagHandler.getBagValue(bench.getID())[1]), 96, 26, 4210752);

		this.itemRender.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.renderEngine, new ItemStack(LootBags.lootbagItem, 1, bench.getID()), 135, 16);

		if (bench.getStorage() < BagHandler.getBagValue(bench.getID())[1]) {
			this.mc.renderEngine.bindTexture(new ResourceLocation("lootbags", "textures/gui/storage_gui.png"));
			this.drawTexturedModalRect(135, 16, 176, 0, 16, 16);
		}
	}
}
