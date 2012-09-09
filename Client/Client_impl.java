/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package maze_game_client;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author ghome
 */
public class Client_impl extends UnicastRemoteObject implements Client_interface {

    public Client_impl() throws RemoteException {
        super();
    }

    @Override
    public boolean notify_info() throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
