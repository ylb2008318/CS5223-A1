/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author ghome
 */
public interface Client_interface extends Remote {
    public boolean notify_info() throws RemoteException;
}