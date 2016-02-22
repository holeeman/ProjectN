/*
 * Leina Engine Server
 * Made By Leina(runway3207@hanmail.net)
 * All rights reserved.
 */
package JEngine;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
public class ClientSocket{
    public long SocketId = -1;
    public boolean Connected = true;
    public DataOutputStream Output;
    public String Name="";
    public Socket Socket;
        public ClientSocket(Socket ClSocket,DataOutputStream ClOutput){
            SocketId = SocketSystem.BindSocket();
            Socket = ClSocket;
            try {
            Output = ClOutput;
            Output.flush();
            Socket.setSoTimeout(1000);
            Socket.setKeepAlive(true);
            }
            catch(IOException e){if(MainServer.ShowError)e.printStackTrace();}
    }
}