import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BusquedaRemota extends Remote {
    int buscarSecuencial(char[] arreglo, char caracterBuscar) throws RemoteException;
    int buscarForkJoin(char[] arreglo, char caracterBuscar) throws RemoteException;
    int buscarConExecutorServices(char[] arreglo, char caracterBuscar) throws RemoteException;
    int registrarCliente() throws RemoteException;
    void enviarArreglo(int clienteID, char[] arreglo) throws RemoteException;
    char[] obtenerArregloCombinado() throws RemoteException;
}