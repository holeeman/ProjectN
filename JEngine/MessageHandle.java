/*
 * Leina Engine Server
 * Made By Leina(runway3207@hanmail.net)
 * All rights reserved.
 */

package JEngine;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageHandle {
    public static void MessageHandle(DataInputStream Input, ByteArrayOutputStream Buffer, DataOutputStream Output, ClientSocket Client, HashMap<Long, ClientSocket> ClientList, LinkedBlockingQueue MessageList){
        boolean IsEqual = false; 
        short MessageId;
	        try {
                    if(Input.available()>0){
                        //System.out.println(Input.readByte()&0xff);
                        IsEqual = ((Input.readByte()&0xff)==138);
                    }
                } catch (IOException e) {
                    //e.printStackTrace();
                    Client.Connected=false;
	            return;
	        }
	        if (IsEqual == true) {
	            try {
                        Client.RoomIndex = ByteBuffer.readShort(Input)&0xffff;
                        MessageId = ByteBuffer.readShort(Input);
	                Input.skipBytes(11);
	                switch (MessageId&0xffff) {
                            case 0:
                                SetMessageId(Buffer, Output, (short)0);
                                ByteBuffer.writeInt(Output,(int)Client.SocketId);
                                SendMessage(Client, Output, Buffer, MessageList);
                                
                                SetMessageId(Buffer, Output, (short)2);
                                ByteBuffer.writeInt(Output,(int)Client.SocketId);
                                SendMessageToAllInRoom(Client, Output, Buffer, MessageList, ClientList);
                                    break;
                            case 2:
                                SetMessageId(Buffer, Output, (short)3);
                                ByteBuffer.writeInt(Output,(int)Client.SocketId);
                                SendMessage(ClientList.get((long)ByteBuffer.readInt(Input)), Output, Buffer, MessageList);
                                    break;
                            case 5:
                                //System.out.println(ByteBuffer.readShort(Input)&0xffff);
                                //System.out.println(ByteBuffer.readShort(Input)&0xffff);
                                SetMessageId(Buffer, Output, (short)5);
                                ByteBuffer.writeInt(Output,(int)Client.SocketId);
                                ByteBuffer.writeShort(Output,ByteBuffer.readShort(Input));
                                ByteBuffer.writeShort(Output,ByteBuffer.readShort(Input));
                                SendMessageToAllInRoomExceptMe(Client, Output, Buffer, MessageList, ClientList);
                                    break;
                            case 6:
                                System.out.println("room: "+Client.RoomIndex);
                                SetMessageId(Buffer, Output, (short)4);
                                ByteBuffer.writeInt(Output, (int)Client.SocketId);
                                SendMessageToAllInRoomExceptMe(Client, Output, Buffer, MessageList, ClientList);
                                break;
                            case 7:
                                System.out.println("room: "+Client.RoomIndex);
                                SetMessageId(Buffer, Output, (short)2);
                                ByteBuffer.writeInt(Output,(int)Client.SocketId);
                                SendMessageToAllInRoomExceptMe(Client, Output, Buffer, MessageList, ClientList);
                                break;
                            case 100:
                                System.out.println(ByteBuffer.readString(Input));
                                SetMessageId(Buffer, Output, (short)101);
                                ByteBuffer.writeString(Output, "asdf");
                                SendMessage(Client, Output, Buffer, MessageList);
                                    break;
                            case 101:
                                //Disconnect
                                System.out.println("test");
                                Client.Connected = false;
                                    break;
                                
	                }
	            }catch (SocketTimeoutException e){
	            	System.out.println("플레이어 "+Client.SocketId+" 타임아웃");
	            	Client.Connected=false;
	            }catch (IOException e){
                        //클라이언트에 메세지를 보낼수 없을때 연결이 끊긴것으로 간주하고 접속종료
                        Client.Connected=false;
	            }catch (InterruptedException e){
                        e.printStackTrace();
                    }
	        }else{
                    //System.out.println("잘못된 헤더");
                }
    }
    static void SetMessageId(ByteArrayOutputStream Buffer, DataOutputStream Output, short Mid) throws IOException{
        Buffer.reset();
        Output.writeShort(Short.reverseBytes(Mid));
    }
    static void SendMessage(ClientSocket Socket, DataOutputStream Output, ByteArrayOutputStream Buffer, LinkedBlockingQueue MessageList) throws IOException, InterruptedException{
        Output.flush();
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        Buffer.writeTo(buf);
        Buffer.reset();
        Message message = new Message(Socket.SocketId, buf);
        MessageList.put(message);
        Socket.Sent ++;
    }
    static void SendMessageToAll(ClientSocket Socket, DataOutputStream Output, ByteArrayOutputStream Buffer, LinkedBlockingQueue MessageList, HashMap<Long, ClientSocket> ClientList) throws IOException, InterruptedException{
        Output.flush();
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        Buffer.writeTo(buf);
        Buffer.reset();
        ClientList.entrySet().stream().forEach((c) -> {
            try{
                c.getValue().Sent ++;
                Message message = new Message(c.getKey(), buf);
                MessageList.put(message);
            } catch(InterruptedException interr){ }
        });
        Buffer.reset();
    }
    static void SendMessageToAllExceptMe(ClientSocket Socket, DataOutputStream Output, ByteArrayOutputStream Buffer, LinkedBlockingQueue MessageList, HashMap<Long, ClientSocket> ClientList) throws IOException, InterruptedException{
        Output.flush();
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        Buffer.writeTo(buf);
        Buffer.reset();
        ClientList.entrySet().stream().forEach((c) -> {
            try{
                c.getValue().Sent ++;
                if(c.getValue() != Socket){
                    Message message = new Message(c.getKey(), buf);
                    MessageList.put(message);
                }
            } catch(InterruptedException interr){ }
        });
    }
    static void SendMessageToAllInRoom(ClientSocket Socket, DataOutputStream Output, ByteArrayOutputStream Buffer, LinkedBlockingQueue MessageList, HashMap<Long, ClientSocket> ClientList) throws IOException, InterruptedException{
        Output.flush();
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        Buffer.writeTo(buf);
        Buffer.reset();
        ClientList.entrySet().stream().forEach((c) -> {
            try{
                c.getValue().Sent ++;
                if(c.getValue().RoomIndex == Socket.RoomIndex){
                    Message message = new Message(c.getKey(), buf);
                    MessageList.put(message);
                }
            } catch(InterruptedException interr){ }
        });
    }
    static void SendMessageToAllInRoomExceptMe(ClientSocket Socket, DataOutputStream Output, ByteArrayOutputStream Buffer, LinkedBlockingQueue MessageList, HashMap<Long, ClientSocket> ClientList) throws IOException, InterruptedException{
        Output.flush();
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        Buffer.writeTo(buf);
        Buffer.reset();
        ClientList.entrySet().stream().forEach((c) -> {
            try{
                c.getValue().Sent ++;
                if(c.getValue().RoomIndex == Socket.RoomIndex && c.getValue() != Socket){
                    Message message = new Message(c.getKey(), buf);
                    MessageList.put(message);
                }
            } catch(InterruptedException interr){ }
        });
    }
}

