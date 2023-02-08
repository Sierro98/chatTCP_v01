package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Servidor implements Runnable  {
    int puerto = 6000;
    private boolean exit;
    private ExecutorService pool;
    private ServerSocket servidor;
    private List<manejadorConexion> conexiones;

    public Servidor() {
        conexiones = new ArrayList<>();
        exit = false;
    }
    @Override
    public void run() {
        try {
            servidor = new ServerSocket(puerto);
            pool = Executors.newCachedThreadPool();
            while (!exit) {
                Socket client = servidor.accept();
                manejadorConexion manejador = new manejadorConexion(client);
                conexiones.add(manejador);
                pool.execute(manejador);
            }
        } catch (IOException e) {
            e.printStackTrace();
            shutdown();
        }
    }
    public void broadcast(String mensaje) {
        for (manejadorConexion manejador : conexiones) {
            if (manejador != null) {
                manejador.enviarMensaje(mensaje);
            }
        }
    }
    public void shutdown() {
        exit = true;
        pool.shutdown();
        if (!servidor.isClosed()) {
            try {
                servidor.close();
            } catch (IOException e) {
                //obviar
            }
            for (manejadorConexion manejador : conexiones) {
                manejador.shutdown();
            }
        }
    }

    //MANEJADOR DE CONEXION
    class manejadorConexion implements Runnable  {
        private Socket client;
        private BufferedReader reader;
        private PrintWriter writer;
        private String nombre;
        public manejadorConexion(Socket client) {
            this.client = client;
        }
        @Override
        public void run() {
            try {
                writer = new PrintWriter(client.getOutputStream(), true);
                reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                writer.println("Seleccione el nombre: ");
                nombre = reader.readLine();
                System.out.println(nombre + " -> se conecto");
                broadcast(nombre + " se unio al chat");
                String mensaje;
                while ((mensaje = reader.readLine()) != null) {
                    if (mensaje.startsWith("/quit")) {
                        broadcast(nombre + " se ha ido del chat");
                        shutdown();
                    } else {
                        broadcast(nombre + ": " + mensaje);
                    }
                }
            } catch (IOException e) {
                shutdown();
            }
        }
        public void enviarMensaje(String mensaje) {
            writer.println(mensaje);
        }
        public void shutdown() {
            try {
                writer.close();
                reader.close();
                if (!client.isClosed()) {
                    client.close();
                }
            } catch (IOException e) {
                //obviar
            }
        }
    }
    public static void main(String[] args) {
        Servidor server = new Servidor();
        server.run();
    }
}