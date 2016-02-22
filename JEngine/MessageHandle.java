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
                    e.printStackTrace();
                    Client.Connected=false;
	            return;
	        }
	        if (IsEqual == true) {
	            try {
	                Input.skipBytes(11);
	                MessageId = ByteBuffer.readShort(Input);
	                switch (MessageId&0xffff) {
                            case 1:
                                SetMessageId(Buffer, Output, (short)1);
                                ByteBuffer.writeInt(Output,(int)Client.SocketId);
                                SendMessage(Client, Output, Buffer, MessageList);
                                
                                SetMessageId(Buffer, Output, (short)101);
                                ByteBuffer.writeString(Output,"new client");
                                SendMessageToAll(Client, Output, Buffer, MessageList, ClientList);
                                    break;
                            case 5:
                                //System.out.println(ByteBuffer.readShort(Input));
                                //System.out.println(ByteBuffer.readShort(Input));
                                    break;
                            case 100:
                                System.out.println(ByteBuffer.readString(Input));
                                SetMessageId(Buffer, Output, (short)101);
                                ByteBuffer.writeString(Output, "asdf");
                                SendMessage(Client, Output, Buffer, MessageList);
                                    break;
                            case 101:
                                //Disconnect
                                Client.Connected = false;
                                    break;
                                
	                }
	            }catch (SocketTimeoutException e){
	            	System.out.println("플레이어 "+Client.SocketId+" 타임아웃");
	            	Client.Connected=false;
	            }catch (IOException e){
	                if(MainServer.ShowError)e.printStackTrace();
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
    }
    static void SendMessageToAll(ClientSocket Socket, DataOutputStream Output, ByteArrayOutputStream Buffer, LinkedBlockingQueue MessageList, HashMap<Long, ClientSocket> ClientList) throws IOException, InterruptedException{
        Output.flush();
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        Buffer.writeTo(buf);
        Buffer.reset();
        ClientList.entrySet().stream().forEach((c) -> {
            try{
                Message message = new Message(c.getKey(), buf);
                MessageList.put(message);
            } catch(InterruptedException interr){ }
        });
        Buffer.reset();
}   }
