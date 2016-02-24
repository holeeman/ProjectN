/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JEngine;

/**
 *
 * @author runway3207
 */
public class Packet {
    final private byte[] data;
    final private ClientSocket client;
    
    public Packet(ClientSocket client, final byte[] data) {
        this.client = client;
        this.data = data;
    }
    
    public final byte[] getBytes() {
        return data;
    }
    
    public void send() {
        try{
            this.client.Output.writeByte(0); //  짤림방지
            this.client.Output.writeByte(138); //  헤더 추가
            ByteBuffer.writeShort(this.client.Output, (short)data.length); // 패킷에 길이 추가
            for(byte b:this.data){
                this.client.Output.writeByte(b);
            }
            this.client.Output.flush();
            //System.out.println("Sent to "+ClientId);
        } catch(Exception ioe){
            try{
                this.client.Connected = false;
            } catch(Exception e){ }
        }
    }
}
