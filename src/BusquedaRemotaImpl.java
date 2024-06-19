import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class BusquedaRemotaImpl extends UnicastRemoteObject implements BusquedaRemota {
    private final List<char[]> arreglosClientes;
    private final List<Integer> clientesConectados;
    private char[] arregloCombinado;
    private boolean arreglosCompletos;
    private final ExecutorService executorService;
    private final ForkJoinPool forkJoinPool;

    public BusquedaRemotaImpl() throws RemoteException {
        arreglosClientes = new CopyOnWriteArrayList<>();
        clientesConectados = new CopyOnWriteArrayList<>();
        arreglosCompletos = false;
        executorService = Executors.newCachedThreadPool();
        forkJoinPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
    }

    @Override
    public synchronized int buscarForkJoin(char[] arreglo, char caracterBuscar) throws RemoteException {
        int threshold = Math.max(1, arreglo.length);
        BusquedaForkJoinTask task = new BusquedaForkJoinTask(arreglo, caracterBuscar, 0, arreglo.length, threshold);
        return forkJoinPool.invoke(task);
    }

    @Override
    public synchronized int buscarConExecutorServices(char[] arreglo, char caracterBuscar) throws RemoteException {
        AtomicInteger contador = new AtomicInteger(0);
        int blockSize = Math.max(1, arreglo.length);
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < arreglo.length; i += blockSize) {
            int start = i;
            int end = Math.min(i + blockSize, arreglo.length);
            futures.add(executorService.submit(() -> {
                for (int j = start; j < end; j++) {
                    if (arreglo[j] == caracterBuscar) {
                        contador.incrementAndGet();
                    }
                }
            }));
        }

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        return contador.get();
    }

    @Override
    public int buscarSecuencial(char[] arreglo, char caracterBuscar) throws RemoteException {
        int contador = 0;
        for (char c : arreglo) {
            if (c == caracterBuscar) {
                contador++;
            }
        }
        return contador;
    }

    @Override
    public synchronized int registrarCliente() throws RemoteException {
        int clienteID = clientesConectados.size() + 1;
        clientesConectados.add(clienteID);
        System.out.println("Cliente id: " + clienteID + " conectado");
        return clienteID;
    }

    @Override
    public synchronized void enviarArreglo(int clienteID, char[] arreglo) throws RemoteException {
        arreglosClientes.add(arreglo);
        System.out.println("Cliente id " + clienteID + " array: " + String.valueOf(arreglo));
        if (arreglosClientes.size() == clientesConectados.size()) {
            //System.out.println("Todos los clientes han enviado sus arrays.");
            arregloCombinado = combinarArreglos(arreglosClientes);
            arreglosCompletos = true;
        }
    }

    private char[] combinarArreglos(List<char[]> arreglos) {
        int totalLength = arreglos.stream().mapToInt(arr -> arr.length).sum();
        char[] combinado = new char[totalLength];
        int index = 0;
        for (char[] arr : arreglos) {
            System.arraycopy(arr, 0, combinado, index, arr.length);
            index += arr.length;
        }
        System.out.println("Array combinado: "+String.valueOf(combinado));
        return combinado;
    }

    @Override
    public synchronized char[] obtenerArregloCombinado() throws RemoteException {
        if (arreglosCompletos) {
            return arregloCombinado;
        } else {
            throw new RemoteException("Los arreglos de todos los clientes aún no están completos.");
        }
    }
}
