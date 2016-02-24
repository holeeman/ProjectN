/*
 * Leina Engine Server
 * Made By Leina(runway3207@hanmail.net)
 * All rights reserved.
 */

package JEngine;
import java.util.HashMap;
import java.util.Collections;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

public class ListenerSocket extends Thread{
    public HashMap<Long, ClientSocket> ClientList;
    public ServerSocket serverSocket;
    private DataInputStream NewInput;
    private DataOutputStream NewOutput;
    final private LinkedBlockingQueue<Packet> PacketSendQueue;
    private ClientSocket NewClient;
    private Socket NewSocket;
    private ServerSender sender = new ServerSender();
        public ListenerSocket( int Port , int MaxClients){
            ClientList = new HashMap();
            PacketSendQueue = new LinkedBlockingQueue();
            Collections.synchronizedMap(ClientList);
                try {
                    serverSocket = new ServerSocket(Port);
                    System.out.println("리스너 개방완료 (포트 : "+Port+")");
                    } catch (IOException e) {
                        System.out.println("소켓개방에 실패하였습니다.");
                        if(MainServer.ShowError)e.printStackTrace();
                    }
        }
        @Override
        public void run(){
            try{
                    sender.setDaemon(true);
                    sender.start();
                    while (true) {
                        NewSocket = serverSocket.accept();
                        NewInput = new DataInputStream(NewSocket.getInputStream());
                        NewOutput = new DataOutputStream(NewSocket.getOutputStream());
                        NewClient = new ClientSocket(NewSocket, NewOutput);
                        ClientList.put(NewClient.SocketId, NewClient);
                        System.out.println("[" + NewSocket.getInetAddress() + ":"
                        + NewSocket.getPort() + "] 플레이어 ("+NewClient.SocketId+")" + "가 접속했습니다.");
                        ServerReceiver receiver = new ServerReceiver(NewClient, NewInput);
                        receiver.start();
                    }
                } catch (IOException e) {
                    System.out.println("플레이어를 받는데 실패하였습니다.");
                    if(MainServer.ShowError)e.printStackTrace();
                }
            finally{
                try{serverSocket.close();}catch(IOException e){if(MainServer.ShowError)e.printStackTrace();}
            }
        }
class ServerReceiver extends Thread {
    //클라이언트 담당
        Socket socket;
        DataInputStream input;
        ClientSocket client;
 
        public ServerReceiver(ClientSocket cl, DataInputStream in) {
            this.socket = cl.Socket;
            try {
                input = in;
                client = cl;
            } catch (Exception e) {if(MainServer.ShowError)System.out.println(e);
            }
        }
 
        @Override
        public void run() {
            do{
                try{
                    if(!this.socket.getKeepAlive()){
                        client.Connected = false;
                    }
                } catch (IOException e){ System.out.print(e);}
                MessageHandle.MessageHandle(input,client,ClientList,PacketSendQueue);
                try{
                    Thread.sleep(10);
                } catch (InterruptedException e){e.printStackTrace();}
	}while(client.Connected);
            try {
                Message message = new Message(4);
                message.writeInt((int)client.SocketId);
                message.SendMessageToAllInRoomExceptMe(client, PacketSendQueue, ClientList);
                
                System.out.println("플레이어 "+client.SocketId+"가 접속을 종료했습니다.");
                ClientList.remove(client.SocketId); 
                input.close();
                SocketSystem.UnbindSocket(client.SocketId);
		socket.close();//Close and Free the socket from memory
        } catch (IOException e) {//If we get an exception we coudn't close the socket
		System.out.println("해당플레이어의 소켓을 닫는중 문제가 발생하였습니다 : "+client.SocketId+".");
        } catch (InterruptedException e){ }
    }
    }
    class ServerSender extends Thread{
        @Override
        public void run() {
            while(true){
                try{
                    Packet packet = PacketSendQueue.take();
                    packet.send();
                } catch(InterruptedException e){ }
            }
        }
    }

}