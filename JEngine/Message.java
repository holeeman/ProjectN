/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JEngine;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
/**
 *
 * @author runway3207
 */
public class Message{
    private ByteArrayOutputStream output;
    private DataOutputStream writer;
    
    public Message(int message_id){
        this(32, message_id);
    }
    
    public Message(int size, int message_id){
        this.output = new ByteArrayOutputStream(size);
        this.writer = new DataOutputStream(this.output);
        try{
            this.writer.writeShort(Short.reverseBytes((short)message_id));
        } catch(IOException e){ }
    }
    
    public void writeByte(byte b){
        try{
            this.writer.writeByte(b);
        } catch(IOException e){ }
    }
    public void writeShort(short s){
        try{
            ByteBuffer.writeShort(this.writer, s);
        } catch(IOException e){ }
    }
    public void writeInt(int i){
        try{
            ByteBuffer.writeInt(this.writer, i);
        } catch(IOException e){ }
    }
    public void writeLong(long l){
        try{
            ByteBuffer.writeLong(this.writer, l);
        } catch(IOException e){ }
    }
    public void writeString(String s){
        ByteBuffer.writeString(this.writer, s);
    }
    
    public Packet getPacket(ClientSocket client){
        return new Packet(client, this.output.toByteArray());
    }
    
    public void SendMessage(ClientSocket client, LinkedBlockingQueue SendingQueue) throws IOException, InterruptedException{
        SendingQueue.put(getPacket(client));
    }
    public void SendMessageToAll(LinkedBlockingQueue SendingQueue, HashMap<Long, ClientSocket> ClientList) throws IOException, InterruptedException{
        ClientList.entrySet().stream().forEach((c) -> {
            try{
                SendingQueue.put(getPacket(c.getValue()));
            } catch(InterruptedException interr){ }
        });
    }
    public void SendMessageToAllExceptMe(ClientSocket client, LinkedBlockingQueue SendingQueue, HashMap<Long, ClientSocket> ClientList) throws IOException, InterruptedException{
        ClientList.entrySet().stream().forEach((c) -> {
            try{
                if(c.getValue() != client){
                    SendingQueue.put(getPacket(c.getValue()));
                }
            } catch(InterruptedException interr){ }
        });
    }
    public void SendMessageToAllInRoom(ClientSocket client, LinkedBlockingQueue SendingQueue, HashMap<Long, ClientSocket> ClientList) throws IOException, InterruptedException{
        ClientList.entrySet().stream().forEach((c) -> {
            try{
                if(c.getValue().RoomIndex == client.RoomIndex){
                    SendingQueue.put(getPacket(c.getValue()));
                }
            } catch(InterruptedException interr){ }
        });
    }
    public void SendMessageToAllInRoomExceptMe(ClientSocket client, LinkedBlockingQueue SendingQueue, HashMap<Long, ClientSocket> ClientList) throws IOException, InterruptedException{
        ClientList.entrySet().stream().forEach((c) -> {
            try{
                if(c.getValue().RoomIndex == client.RoomIndex && c.getValue() != client){
                    SendingQueue.put(getPacket(c.getValue()));
                }
            } catch(InterruptedException interr){ }
        });
    }
    
    public void test(){
        System.out.println("start");
            for (byte b:this.output.toByteArray()){
                System.out.println(b&0xff);
            }
            System.out.println("end");
    }
}
