package mal.lootbags.network.message;

import io.netty.buffer.ByteBuf;
import mal.lootbags.tileentity.TileEntityOpener;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class OpenerMessageServer implements IMessage, IMessageHandler<OpenerMessageServer, IMessage> {

	public int xpos, ypos, zpos;
	public int cooldown;

	public OpenerMessageServer() {}
	public OpenerMessageServer(TileEntityOpener te, int cd) {
		this.cooldown = cd;
		this.xpos = te.xCoord;
		this.ypos = te.yCoord;
		this.zpos = te.zCoord;
	}

	@Override
	public IMessage onMessage(OpenerMessageServer message, MessageContext ctx) {
		TileEntity te = FMLClientHandler.instance().getWorldClient().getTileEntity(message.xpos, message.ypos, message.zpos);
		if (te instanceof TileEntityOpener) {
			((TileEntityOpener) te).setData(message.cooldown);
		}
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		xpos = buf.readInt();
		ypos = buf.readInt();
		zpos = buf.readInt();
		cooldown = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(xpos);
		buf.writeInt(ypos);
		buf.writeInt(zpos);
		buf.writeInt(cooldown);
	}
}
