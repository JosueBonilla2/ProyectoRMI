import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;

public class Cliente extends JFrame {
    private JTextArea textoOriginalArea, textoResultadoArea, textoArregloServidorArea;
    private JTextField caracterBuscarField, tamanoArregloField;
    private JLabel caracteresEncontradosLabelSecuencial, caracteresEncontradosLabelForkJoin, caracteresEncontradosLabelExecutor, tiempoLabelSecuencial, tiempoLabelForkJoin, tiempoLabelExecutor, tamanoArregloServidorLabel;
    private char[] arreglo;
    private int clienteID;
    private BusquedaRemota stub;

    public Cliente() {
        super("Cliente de Comparación de Algoritmos de Búsqueda");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            Registry registry = LocateRegistry.getRegistry("192.168.1.189");
            stub = (BusquedaRemota) registry.lookup("BusquedaRemota");
            clienteID = stub.registrarCliente();
        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BorderLayout());

        JPanel panelEntrada = new JPanel();
        panelEntrada.setLayout(new GridLayout(8, 2));

        JLabel tamanoArregloLabel = new JLabel("Tamaño del arreglo:");
        tamanoArregloField = new JTextField(5);
        JButton generarArregloButton = new JButton("Generar Arreglo");
        JLabel caracterBuscarLabel = new JLabel("Caracter a buscar:");
        caracterBuscarField = new JTextField(5);
        JButton secuencialButton = new JButton("Secuencial");
        JButton forkJoinButton = new JButton("Fork/Join");
        JButton executorServicesButton = new JButton("ExecutorServices");

        panelEntrada.add(tamanoArregloLabel);
        panelEntrada.add(tamanoArregloField);
        panelEntrada.add(new JLabel());
        panelEntrada.add(generarArregloButton);
        panelEntrada.add(new JLabel());
        panelEntrada.add(new JLabel());
        panelEntrada.add(caracterBuscarLabel);
        panelEntrada.add(caracterBuscarField);
        panelEntrada.add(new JLabel());
        panelEntrada.add(secuencialButton);
        panelEntrada.add(new JLabel());
        panelEntrada.add(forkJoinButton);
        panelEntrada.add(new JLabel());
        panelEntrada.add(executorServicesButton);

        JPanel panelSalida = new JPanel();
        panelSalida.setLayout(new GridLayout(3, 1));

        JPanel panelTextoOriginal = new JPanel();
        panelTextoOriginal.setLayout(new BorderLayout());
        textoOriginalArea = new JTextArea(5, 40);
        JScrollPane scrollOriginal = new JScrollPane(textoOriginalArea);
        panelTextoOriginal.add(new JLabel("Texto Original:"), BorderLayout.NORTH);
        panelTextoOriginal.add(scrollOriginal, BorderLayout.CENTER);

        JPanel panelTextoResultado = new JPanel();
        panelTextoResultado.setLayout(new BorderLayout());
        textoResultadoArea = new JTextArea(5, 40);
        JScrollPane scrollResultado = new JScrollPane(textoResultadoArea);
        panelTextoResultado.add(new JLabel("Texto Resultado:"), BorderLayout.NORTH);
        panelTextoResultado.add(scrollResultado, BorderLayout.CENTER);

        JPanel panelTextoServidor = new JPanel();
        panelTextoServidor.setLayout(new BorderLayout());
        textoArregloServidorArea = new JTextArea(5, 40);
        JScrollPane scrollServidor = new JScrollPane(textoArregloServidorArea);
        panelTextoServidor.add(new JLabel("Arreglo desde el Servidor:"), BorderLayout.NORTH);
        panelTextoServidor.add(scrollServidor, BorderLayout.CENTER);
        tamanoArregloServidorLabel = new JLabel();

        panelSalida.add(panelTextoOriginal);
        panelSalida.add(panelTextoResultado);
        panelSalida.add(panelTextoServidor);

        JPanel panelInfo = new JPanel();
        panelInfo.setLayout(new GridLayout(10, 2));
        JLabel caracteresEncontradosSecuencialLabel = new JLabel("Caracteres encontrados (Secuencial): ");
        caracteresEncontradosLabelSecuencial = new JLabel();
        JLabel caracteresEncontradosForkJoinLabel = new JLabel("Caracteres encontrados (Fork/Join): ");
        caracteresEncontradosLabelForkJoin = new JLabel();
        JLabel caracteresEncontradosExecutorLabel = new JLabel("Caracteres encontrados (ExecutorServices): ");
        caracteresEncontradosLabelExecutor = new JLabel();
        JLabel tiempoSecuencialLabel = new JLabel("Tiempo (Secuencial): ");
        tiempoLabelSecuencial = new JLabel();
        JLabel tiempoForkJoinLabel = new JLabel("Tiempo (Fork/Join): ");
        tiempoLabelForkJoin = new JLabel();
        JLabel tiempoExecutorLabel = new JLabel("Tiempo (ExecutorServices): ");
        tiempoLabelExecutor = new JLabel();
        JButton limpiar = new JButton("Limpiar");

        panelInfo.add(new JLabel());
        panelInfo.add(new JLabel());
        panelInfo.add(caracteresEncontradosSecuencialLabel);
        panelInfo.add(caracteresEncontradosLabelSecuencial);
        panelInfo.add(caracteresEncontradosForkJoinLabel);
        panelInfo.add(caracteresEncontradosLabelForkJoin);
        panelInfo.add(caracteresEncontradosExecutorLabel);
        panelInfo.add(caracteresEncontradosLabelExecutor);
        panelInfo.add(tiempoSecuencialLabel);
        panelInfo.add(tiempoLabelSecuencial);
        panelInfo.add(tiempoForkJoinLabel);
        panelInfo.add(tiempoLabelForkJoin);
        panelInfo.add(tiempoExecutorLabel);
        panelInfo.add(tiempoLabelExecutor);
        panelInfo.add(new JLabel("Tamaño del arreglo combinado del servidor:"));
        panelInfo.add(tamanoArregloServidorLabel);
        panelInfo.add(new JLabel());
        panelInfo.add(limpiar);

        panelPrincipal.add(panelEntrada, BorderLayout.NORTH);
        panelPrincipal.add(panelSalida, BorderLayout.CENTER);
        panelPrincipal.add(panelInfo, BorderLayout.SOUTH);

        add(panelPrincipal);

        generarArregloButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int tamano = Integer.parseInt(tamanoArregloField.getText());
                arreglo = generarArreglo(tamano);
                textoOriginalArea.setText(new String(arreglo));
                try {
                    stub.enviarArreglo(clienteID, arreglo);
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        });

        secuencialButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    long startTime = System.nanoTime();
                    char[] arregloCombinado = stub.obtenerArregloCombinado();
                    int count = stub.buscarSecuencial(arregloCombinado, caracterBuscarField.getText().charAt(0));
                    long endTime = System.nanoTime();
                    double duration = (endTime - startTime) / 1e5;

                    textoArregloServidorArea.setText(new String(arregloCombinado));
                    caracteresEncontradosLabelSecuencial.setText(String.valueOf(count));
                    tiempoLabelSecuencial.setText(duration + " ms");
                    tamanoArregloServidorLabel.setText(arregloCombinado.length + " caracteres");

                    StringBuilder resultado = new StringBuilder();
                    for (char c : arregloCombinado) {
                        if (c == caracterBuscarField.getText().charAt(0)) {
                            resultado.append(c);
                        }
                    }
                    textoResultadoArea.setText(resultado.toString());

                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        });

        forkJoinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    long startTime = System.nanoTime();
                    char[] arregloCombinado = stub.obtenerArregloCombinado();
                    int count = stub.buscarForkJoin(arregloCombinado, caracterBuscarField.getText().charAt(0));
                    long endTime = System.nanoTime();
                    double duration = (endTime - startTime) / 1e5;

                    textoArregloServidorArea.setText(new String(arregloCombinado));
                    caracteresEncontradosLabelForkJoin.setText(String.valueOf(count));
                    tiempoLabelForkJoin.setText(duration + " ms");
                    tamanoArregloServidorLabel.setText(arregloCombinado.length + " caracteres");
                    StringBuilder resultado = new StringBuilder();
                    for (char c : arregloCombinado) {
                        if (c == caracterBuscarField.getText().charAt(0)) {
                            resultado.append(c);
                        }
                    }
                    textoResultadoArea.setText(resultado.toString());

                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        });

        executorServicesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    long startTime = System.nanoTime();
                    char[] arregloCombinado = stub.obtenerArregloCombinado();
                    int count = stub.buscarConExecutorServices(arregloCombinado, caracterBuscarField.getText().charAt(0));
                    long endTime = System.nanoTime();
                    double duration = (endTime - startTime)/ 1e5;

                    textoArregloServidorArea.setText(new String(arregloCombinado));
                    caracteresEncontradosLabelExecutor.setText(String.valueOf(count));
                    tiempoLabelExecutor.setText(duration + " ms");
                    tamanoArregloServidorLabel.setText(arregloCombinado.length + " caracteres");

                    StringBuilder resultado = new StringBuilder();
                    for (char c : arregloCombinado) {
                        if (c == caracterBuscarField.getText().charAt(0)) {
                            resultado.append(c);
                        }
                    }
                    textoResultadoArea.setText(resultado.toString());

                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        });

        limpiar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiar();
            }
        });

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                setLocationRelativeTo(null);
                setVisible(true);
            }
        });
    }

    private char[] generarArreglo(int tamano) {
        char[] arreglo = new char[tamano];
        Random random = new Random();
        for (int i = 0; i < tamano; i++) {
            arreglo[i] = (char) (random.nextInt(93) + 33);
        }
        return arreglo;
    }

    private void limpiar() {
        tamanoArregloField.setText("");
        caracterBuscarField.setText("");
        textoOriginalArea.setText("");
        textoResultadoArea.setText("");
        textoArregloServidorArea.setText("");
        caracteresEncontradosLabelSecuencial.setText("");
        caracteresEncontradosLabelForkJoin.setText("");
        caracteresEncontradosLabelExecutor.setText("");
        tiempoLabelSecuencial.setText("");
        tiempoLabelForkJoin.setText("");
        tiempoLabelExecutor.setText("");
        tamanoArregloServidorLabel.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Cliente();
            }
        });
    }
}

