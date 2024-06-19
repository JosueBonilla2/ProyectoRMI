import java.util.concurrent.RecursiveTask;

public class BusquedaForkJoinTask extends RecursiveTask<Integer> {
    private char[] arreglo;
    private char caracterBuscar;
    private int inicio;
    private int fin;
    private int threshold;

    public BusquedaForkJoinTask(char[] arreglo, char caracterBuscar, int inicio, int fin, int threshold) {
        this.arreglo = arreglo;
        this.caracterBuscar = caracterBuscar;
        this.inicio = inicio;
        this.fin = fin;
        this.threshold = threshold;
    }

    @Override
    protected Integer compute() {
        if (fin - inicio <= threshold) {
            int contador = 0;
            for (int i = inicio; i < fin; i++) {
                if (arreglo[i] == caracterBuscar) {
                    contador++;
                }
            }
            return contador;
        } else {
            int mitad = inicio + (fin - inicio) / 2;
            BusquedaForkJoinTask leftTask = new BusquedaForkJoinTask(arreglo, caracterBuscar, inicio, mitad, threshold);
            BusquedaForkJoinTask rightTask = new BusquedaForkJoinTask(arreglo, caracterBuscar, mitad, fin, threshold);

            leftTask.fork();
            int rightResult = rightTask.compute();
            int leftResult = leftTask.join();

            return leftResult + rightResult;
        }
    }
}
