package mal.lootbags.network.message;

import io.netty.buffer.ByteBuf;
import mal.lootbags.tileentity.TileEntityStorage;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class StorageMessageServer implements IMessage, IMessageHandler<StorageMessageServer, IMessage> {

	public int xpos, ypos, zpos;
	public int stored_value, outputID, outputindex;

	public StorageMessageServer() {}
	public StorageMessageServer(TileEntityStorage te, int stored_value, int outputID, int outputindex) {
		this.xpos = te.xCoord;
		this.ypos = te.yCoord;
		this.zpos = te.zCoord;
		this.stored_value = stored_value;
		this.outputID = outputID;
		this.outputindex = outputindex;
	}

	@Override
	public IMessage onMessage(StorageMessageServer message, MessageContext ctx) {
		TileEntity te = FMLClientHandler.instance().getWorldClient().getTileEntity(message.xpos, message.ypos, message.zpos);
		if (te instanceof TileEntityStorage) {
			((TileEntityStorage) te).setDataClient(message.stored_value, message.outputID, message.outputindex);
		}
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		xpos = buf.readInt();
		ypos = buf.readInt();
		zpos = buf.readInt();
		stored_value = buf.readInt();
		outputID = buf.readInt();
		outputindex = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(xpos);
		buf.writeInt(ypos);
		buf.writeInt(zpos);
		buf.writeInt(stored_value);
		buf.writeInt(outputID);
		buf.writeInt(outputindex);
	}
}
