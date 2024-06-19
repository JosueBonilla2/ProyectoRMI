import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Servidor {
    public static void main(String[] args) {
        try {
            BusquedaRemotaImpl obj = new BusquedaRemotaImpl();
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("BusquedaRemota", obj);
            System.out.println("Servidor listo");
        } catch (Exception e) {
            System.err.println("Error en el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
