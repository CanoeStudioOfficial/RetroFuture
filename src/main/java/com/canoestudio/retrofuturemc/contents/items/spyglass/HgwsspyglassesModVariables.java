package com.canoestudio.retrofuturemc.contents.items.spyglass;

import com.canoestudio.retrofuturemc.RetroFuturemc;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class HgwsspyglassesModVariables {

    //========== 地图数据（每个维度独立存储） ==========//
    public static class MapVariables extends WorldSavedData {
        public static final String DATA_NAME = "hgwsspyglasses_mapvars";

        public MapVariables() {
            super(DATA_NAME);
        }

        public MapVariables(String name) {
            super(name);
        }

        @Override
        public void readFromNBT(NBTTagCompound nbt) {
            // 读取NBT数据（需自定义实现）
        }

        @Override
        public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
            // 写入NBT数据（需自定义实现）
            return nbt;
        }

        /**
         * 同步数据到客户端/服务端
         */
        public void syncData(World world) {
            markDirty(); // 标记数据需要保存

            if (world.isRemote) {
                // 客户端 -> 发送给服务端
                RetroFuturemc.NETWORK.sendToServer(
                        new WorldSavedDataSyncMessage(0, this)
                );
            } else {
                // 服务端 -> 广播给所有玩家
                RetroFuturemc.NETWORK.sendToAll(
                        new WorldSavedDataSyncMessage(0, this)
                );
            }
        }

        /**
         * 获取或创建地图数据
         */
        public static MapVariables get(World world) {
            MapVariables instance = (MapVariables) world.getMapStorage()
                    .getOrLoadData(MapVariables.class, DATA_NAME);

            if (instance == null) {
                instance = new MapVariables();
                world.getMapStorage().setData(DATA_NAME, instance);
            }
            return instance;
        }
    }

    //========== 世界数据（全局存储） ==========//
    public static class WorldVariables extends WorldSavedData {
        public static final String DATA_NAME = "hgwsspyglasses_worldvars";

        public WorldVariables() {
            super(DATA_NAME);
        }

        public WorldVariables(String name) {
            super(name);
        }

        @Override
        public void readFromNBT(NBTTagCompound nbt) {
            // 读取NBT数据（需自定义实现）
        }

        @Override
        public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
            // 写入NBT数据（需自定义实现）
            return nbt;
        }

        /**
         * 同步数据到客户端/服务端
         */
        public void syncData(World world) {
            markDirty(); // 标记数据需要保存

            if (world.isRemote) {
                // 客户端 -> 发送给服务端
                RetroFuturemc.NETWORK.sendToServer(
                        new WorldSavedDataSyncMessage(1, this)
                );
            } else {
                // 服务端 -> 广播到当前维度
                RetroFuturemc.NETWORK.sendToDimension(
                        new WorldSavedDataSyncMessage(1, this),
                        world.provider.getDimension()
                );
            }
        }

        /**
         * 获取或创建世界数据
         */
        public static WorldVariables get(World world) {
            WorldVariables instance = (WorldVariables) world.getPerWorldStorage()
                    .getOrLoadData(WorldVariables.class, DATA_NAME);

            if (instance == null) {
                instance = new WorldVariables();
                world.getPerWorldStorage().setData(DATA_NAME, instance);
            }
            return instance;
        }
    }

    //========== 网络消息处理器 ==========//
    public static class WorldSavedDataSyncMessageHandler
            implements IMessageHandler<WorldSavedDataSyncMessage, IMessage> {

        @Override
        public IMessage onMessage(WorldSavedDataSyncMessage message, MessageContext context) {
            if (context.side == Side.SERVER) {
                // 服务端处理
                context.getServerHandler().player.getServerWorld()
                        .addScheduledTask(() ->
                                syncData(message, context, context.getServerHandler().player.world)
                        );
            } else {
                // 客户端处理
                Minecraft.getMinecraft().addScheduledTask(() ->
                        syncData(message, context, Minecraft.getMinecraft().player.world)
                );
            }
            return null;
        }

        private void syncData(WorldSavedDataSyncMessage message, MessageContext context, World world) {
            if (context.side == Side.SERVER) {
                message.data.markDirty();
                if (message.type == 0) {
                    RetroFuturemc.NETWORK.sendToAll(message);
                } else {
                    RetroFuturemc.NETWORK.sendToDimension(
                            message,
                            world.provider.getDimension()
                    );
                }
            }

            // 保存数据到世界存储
            if (message.type == 0) {
                world.getMapStorage().setData("hgwsspyglasses_mapvars", message.data);
            } else {
                world.getPerWorldStorage().setData("hgwsspyglasses_worldvars", message.data);
            }
        }
    }

    //========== 网络消息定义 ==========//
    public static class WorldSavedDataSyncMessage implements IMessage {
        public int type; // 0=地图数据, 1=世界数据
        public WorldSavedData data;

        public WorldSavedDataSyncMessage() {}

        public WorldSavedDataSyncMessage(int type, WorldSavedData data) {
            this.type = type;
            this.data = data;
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeInt(this.type);
            ByteBufUtils.writeTag(buf, this.data.writeToNBT(new NBTTagCompound()));
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            this.type = buf.readInt();
            this.data = (this.type == 0)
                    ? new MapVariables()
                    : new WorldVariables();
            this.data.readFromNBT(ByteBufUtils.readTag(buf));
        }
    }
}