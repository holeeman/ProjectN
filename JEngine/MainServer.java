/*
 * Leina Engine Server
 * Made By Leina(runway3207@hanmail.net)
 * All rights reserved.
 */
package JEngine;
import java.util.Scanner;
import java.util.HashMap;

public class MainServer {
    static Scanner s = new Scanner(System.in);
    public static long StartTime = System.currentTimeMillis();
    static HashMap<Long, ClientSocket> Clientlist;
    static ListenerSocket Server;
    static String Str="";
    static String[] Com;
    static boolean ShowError = true;
    public static void main(String[] args){
        //서버구동
        try{
            //MySql.GetConnection();
            Server= new ListenerSocket(12345,255);
            Server.start();
            Clientlist = Server.ClientList;
            System.out.println("서버구동완료 (구동시간 : "+(System.currentTimeMillis()-StartTime)+"ms)");
        }catch(Exception e){if(ShowError)e.printStackTrace();}
        //커맨드라인
        while(true){
            Str=s.nextLine();
            if("exit".equals(Str))
                break;
            Com = Str.split(" ",5);
            try{
                Commands.Execute(Com,Clientlist);
            } catch(Exception e){e.printStackTrace();}
        }
        //서버종료
        try{
        MySql.Close();
        for(int i=0;i<Server.ClientList.size();i++){
            try{
                Server.ClientList.get((long)i).Socket.close();
            } catch(Exception e){ }
        }
        System.out.println("클라이언트 연결해제");
        Server.serverSocket.close();
        System.out.println("리스닝 소켓닫힘");
        }catch(Exception e){if(ShowError) e.printStackTrace();}
    }
}
