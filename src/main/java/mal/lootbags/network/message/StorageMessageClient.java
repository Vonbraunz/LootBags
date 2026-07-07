package mal.lootbags.network.message;

import io.netty.buffer.ByteBuf;
import mal.lootbags.tileentity.TileEntityStorage;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class StorageMessageClient implements IMessage, IMessageHandler<StorageMessageClient, IMessage> {

	public int xpos, ypos, zpos;
	public int outputID, outputindex;

	public StorageMessageClient() {}
	public StorageMessageClient(TileEntityStorage te, int outputID, int outputindex) {
		this.xpos = te.xCoord;
		this.ypos = te.yCoord;
		this.zpos = te.zCoord;
		this.outputID = outputID;
		this.outputindex = outputindex;
	}

	@Override
	public IMessage onMessage(StorageMessageClient message, MessageContext ctx) {
		TileEntity te = ctx.getServerHandler().playerEntity.worldObj.getTileEntity(message.xpos, message.ypos, message.zpos);
		if (te instanceof TileEntityStorage) {
			((TileEntityStorage) te).setDataServer(message.outputID, message.outputindex);
		}
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		xpos = buf.readInt();
		ypos = buf.readInt();
		zpos = buf.readInt();
		outputID = buf.readInt();
		outputindex = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(xpos);
		buf.writeInt(ypos);
		buf.writeInt(zpos);
		buf.writeInt(outputID);
		buf.writeInt(outputindex);
	}
}
