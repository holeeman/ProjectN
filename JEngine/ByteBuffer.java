/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JEngine;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author HS
 */
public class ByteBuffer {
    public static String readString(DataInputStream Input){
        String Str=null;
        try{Str= Input.readUTF();Input.skipBytes(1);}catch(IOException e){if(MainServer.ShowError)e.printStackTrace();}
        return Str;
    }
    public static String writeString(DataOutputStream Output, String string){
        String Str=null;
        try{Output.writeUTF(string);Output.writeByte(0);}catch(IOException e){if(MainServer.ShowError)e.printStackTrace();}
        return Str;
    }
    public static short readShort(DataInputStream Input) throws IOException{
        return Short.reverseBytes(Input.readShort());
    }
    public static void writeShort(DataOutputStream Output, short Int) throws IOException{
        Output.writeShort(Short.reverseBytes(Int));
    }
    public static int readInt(DataInputStream Input) throws IOException{
        return Integer.reverseBytes(Input.readInt());
    }
    public static void writeInt(DataOutputStream Output, int Int) throws IOException{
        Output.writeInt(Integer.reverseBytes(Int));
    }
    public static long readLong(DataInputStream Input) throws IOException{
        return Long.reverseBytes(Input.readLong());
    }
    public static void writeLong(DataOutputStream Output, long Int) throws IOException{
        Output.writeLong(Long.reverseBytes(Int));
    }
}
