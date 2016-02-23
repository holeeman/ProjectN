/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JEngine;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
/**
 *
 * @author runway3207
 */
public class Message{
    final private long ClientId;
    final public ByteArrayOutputStream Output;
    public Message(long id , ByteArrayOutputStream out){
        ClientId = id;
        Output = out;
    }
    public void send(HashMap<Long, ClientSocket> ClientList){
        try{
            ClientList.get(ClientId).Output.writeByte(138); //  헤더 추가
            ByteBuffer.writeShort(ClientList.get(ClientId).Output, (short)Output.toByteArray().length); // 패킷에 길이 추가
            //System.out.println(Output.toByteArray().length);
            for(byte b:Output.toByteArray()){
                ClientList.get(ClientId).Output.writeByte(b);
            }
            ClientList.get(ClientId).Output.flush();
            //System.out.println("Sent to "+ClientId);
            Output.close();
        } catch(Exception ioe){
            try{
                ClientList.get(ClientId).Connected = false;
            } catch(Exception e){ }
        }
    }
    public void test(){
        System.out.println("Send To "+ClientId);
        System.out.println("start");
            for (byte b:Output.toByteArray()){
                System.out.println(b&0xff);
            }
            System.out.println("end");
    }
}
