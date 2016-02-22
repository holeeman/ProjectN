/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JEngine;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author HS
 */
public class SocketSystem {
    public static List<Long> SocketList = new ArrayList<Long>();
    public static int SocketAssigner = 0;
    public static long BindSocket() {
			long SocketId = -1;

			if ( SocketList.isEmpty()) {
				SocketId = SocketAssigner;
				SocketAssigner ++;
			} else {
				SocketId = SocketList.get(0);
				SocketList.remove(0);
			}

			return SocketId;
		}
    public static void UnbindSocket( long SocketId ) {
			if ( SocketId >= 0 ) {
				SocketList.add(SocketId);
				Collections.sort(SocketList);
			}
		}
}
