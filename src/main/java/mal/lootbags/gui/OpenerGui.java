package mal.lootbags.gui;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import mal.lootbags.LootBags;
import mal.lootbags.tileentity.TileEntityOpener;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class OpenerGui extends GuiContainer {

	TileEntityOpener bench;

	public OpenerGui(InventoryPlayer player, TileEntityOpener te) {
		super(new OpenerContainer(player, te));
		bench = te;
		ySize = 186;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(new ResourceLocation("lootbags", "textures/gui/opener_Gui.png"));
		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		this.fontRendererObj.drawString("Lootbag Opener", 52, 5, 4210752);

		if (LootBags.OPENERMAXCOOLDOWN > 0) {
			int red = (int) Math.floor(255 * ((float) bench.getCooldown()) / LootBags.OPENERMAXCOOLDOWN);
			int green = 155 - (int) Math.floor(155 * ((float) bench.getCooldown()) / LootBags.OPENERMAXCOOLDOWN);
			Color color = new Color(red, green, 0, 255);
			int width = 162 - (int) Math.floor(162 * ((float) bench.getCooldown()) / LootBags.OPENERMAXCOOLDOWN);
			Gui.drawRect(7, 37, 7 + width, 41, color.getRGB());
		}
	}
}
