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
    public static void MessageHandle(DataInputStream Input, ClientSocket Client, HashMap<Long, ClientSocket> ClientList, LinkedBlockingQueue MessageList){
        boolean IsEqual = false; 
        short MessageId;
        Message message;
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
                                message = new Message(0);
                                message.writeInt((int)Client.SocketId);
                                message.SendMessage(Client, MessageList);
                                
                                message = new Message(2);
                                message.writeInt((int)Client.SocketId);
                                message.SendMessageToAllInRoom(Client, MessageList, ClientList);
                                    break;
                            case 2:
                                message = new Message(3);
                                message.writeInt((int)Client.SocketId);
                                message.SendMessage(ClientList.get((long)ByteBuffer.readShort(Input)), MessageList);
                                    break;
                            case 5:
                                //System.out.println(ByteBuffer.readShort(Input)&0xffff);
                                //System.out.println(ByteBuffer.readShort(Input)&0xffff);
                                message = new Message(5);
                                message.writeInt((int)Client.SocketId);
                                message.writeShort(ByteBuffer.readShort(Input));
                                message.writeShort(ByteBuffer.readShort(Input));
                                message.SendMessageToAllInRoomExceptMe(Client, MessageList, ClientList);
                                    break;
                            case 6:
                                System.out.println("room: "+Client.RoomIndex);
                                message = new Message(4);
                                message.writeInt((int)Client.SocketId);
                                message.SendMessageToAllInRoomExceptMe(Client, MessageList, ClientList);
                                break;
                            case 7:
                                System.out.println("room: "+Client.RoomIndex);
                                message = new Message(2);
                                message.writeInt((int)Client.SocketId);
                                message.SendMessageToAllInRoomExceptMe(Client, MessageList, ClientList);
                                break;
                            case 100:
                                System.out.println(ByteBuffer.readString(Input));
                                message = new Message(101);
                                message.writeString("asdf");
                                message.SendMessage(Client, MessageList);
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
}

