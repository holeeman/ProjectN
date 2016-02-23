/*
 * Leina Engine Server
 * Made By Leina(runway3207@hanmail.net)
 * All rights reserved.
 */

package JEngine;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class Commands {
    public static List<String> Logs = new ArrayList<String>();
    public static void Execute(String[] Command, HashMap<Long, ClientSocket> ClientList){
        switch(Command[0])
        {
            case "로그추가":
                if(Command.length>1){
                    AddLog(StrMerge(Command,1));
                    }
                else{
                    AddLine("로그추가 <로그>");
                }
                    break;
            case "강퇴":
                if(Command.length>1){
                    for(int i=0; i<ClientList.size();i++){
                        if(ClientList.get(i).Name==Command[1]){
                            AddLine(Command[1]+" 강퇴완료");break;
                        }
                    }AddLine("해당 플레이어는 존재하지 않습니다.");break;
                    }
                else{
                    AddLine("강퇴 <플레이어>");
                }
                    break;
            case "로그보기":
            	for (String _log: Logs)
            		AddLine(_log);
                    break;
            case "로그초기화":
                Logs.clear();
                    break;
            case "접속자수":
                System.out.println(ClientList.size());
                    break;
            case "debug":
                System.out.println(ClientList.get((long)Integer.parseInt(Command[1])).Sent);
                    break;
            default:
                AddLine("존재하지 않는 명령어입니다. 명령어 확인은 도움말을 입력해주세요.");
                    break;
        }
    }
    public static void AddLog(String Log){
        Logs.add(Log);
        if (Logs.size()>=100){
            Logs.remove(0);
        }
    }
    public static void AddLine(String Line){
        System.out.println(Line);
    }
    private static String StrMerge(String[] Str, int Ind){
        String _str="";
            for(int i=Ind;i<Str.length;i++){
                _str=_str+" "+Str[i];
            }
        return _str;
    }
}
