package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Servidor implements Runnable {
    int puerto = 6000;
    private boolean exit;
    private ExecutorService pool;
    private ServerSocket servidor;
    private List<manejadorConexion> conexiones;
    private HashMap<String, Socket> listaUsuarios;
    private List<HashMap<String, Socket>> listaGrupos;
    private HashMap<String, Socket> grupo1;
    private HashMap<String, Socket> grupo2;
    private HashMap<String, Socket> grupo3;
    private HashMap<String, Socket> grupo4;
    private HashMap<String, Socket> grupo5;

    public Servidor() {
        conexiones = new ArrayList<>();
        exit = false;
        listaUsuarios = new HashMap<>();
        listaGrupos = new ArrayList<>();
        grupo1 = new HashMap<>();
        grupo2 = new HashMap<>();
        grupo3 = new HashMap<>();
        grupo4 = new HashMap<>();
        grupo5 = new HashMap<>();
        listaGrupos.add(grupo1);
        listaGrupos.add(grupo2);
        listaGrupos.add(grupo3);
        listaGrupos.add(grupo4);
        listaGrupos.add(grupo5);
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
        } catch (Exception e) {
            e.printStackTrace();
            shutdown();
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
    class manejadorConexion implements Runnable {
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
                listaUsuarios.put(nombre, client);
                String mensaje;
                while ((mensaje = reader.readLine()) != null) {
                    if (mensaje.startsWith("/quit")) {
                        broadcast(nombre + " se ha ido del chat");
                        System.out.println(nombre + " -> se desconecto");
                        shutdown();
                    } else if (mensaje.startsWith("/dm")) {
                        String[] direct2 = mensaje.split(" ");
                        Socket socket = listaUsuarios.get(direct2[1]);
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                        StringBuilder msg = new StringBuilder();
                        for (int i = 2; i < direct2.length; i++) {
                            msg.append(direct2[i]);
                            msg.append(" ");
                        }
                        out.println(nombre + ": " + msg);
                    } else if (mensaje.startsWith("/groups")) {
                        enviarMensaje("1. grupo1\n" +
                                "2. grupo2\n" +
                                "3. grupo3\n" +
                                "4.4grupo4\n" +
                                "5. grupo5");
                    } else if (mensaje.startsWith("/join")) {
                        String[] direct2 = mensaje.split(" ");
                        for (HashMap<String, Socket> grupo : listaGrupos) {
                            if (direct2[1].equals("grupo1")) {
                                grupo.put(nombre, client);
                                enviarMensaje("Te uniste al grupo");
                            } else if (direct2[1].equals("grupo2")) {
                                grupo.put(nombre, client);
                                enviarMensaje("Te uniste al grupo");
                            } else if (direct2[1].equals("grupo3")) {
                                grupo.put(nombre, client);
                                enviarMensaje("Te uniste al grupo");
                            } else if (direct2[1].equals("grupo4")) {
                                grupo.put(nombre, client);
                                enviarMensaje("Te uniste al grupo");
                            } else if (direct2[1].equals("grupo5")) {
                                grupo.put(nombre, client);
                                enviarMensaje("Te uniste al grupo");
                            } else {
                                enviarMensaje("Grupo invalido");
                            }
                        }
                    } else if (mensaje.startsWith("/grupo1")) {
                        String[] direct2 = mensaje.split(" ");
                        grupo1.forEach(
                                (key, value)
                                        -> {
                                    try {
                                        PrintWriter out = new PrintWriter(value.getOutputStream(), true);
                                        StringBuilder msg = new StringBuilder();
                                        for (int i = 1; i < direct2.length; i++) {
                                            msg.append(direct2[i]);
                                            msg.append(" ");
                                        }
                                        out.println(nombre + ": " + msg);
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                        );
                    } else if (mensaje.startsWith("/grupo2")) {
                        String[] direct2 = mensaje.split(" ");
                        grupo2.forEach(
                                (key, value)
                                        -> {
                                    try {
                                        PrintWriter out = new PrintWriter(value.getOutputStream(), true);
                                        StringBuilder msg = new StringBuilder();
                                        for (int i = 1; i < direct2.length; i++) {
                                            msg.append(direct2[i]);
                                            msg.append(" ");
                                        }
                                        out.println(nombre + ": " + msg);
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                        );
                    } else if (mensaje.startsWith("/grupo3")) {
                        String[] direct2 = mensaje.split(" ");
                        grupo3.forEach(
                                (key, value)
                                        -> {
                                    try {
                                        PrintWriter out = new PrintWriter(value.getOutputStream(), true);
                                        StringBuilder msg = new StringBuilder();
                                        for (int i = 1; i < direct2.length; i++) {
                                            msg.append(direct2[i]);
                                            msg.append(" ");
                                        }
                                        out.println(nombre + ": " + msg);
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                        );
                    } else if (mensaje.startsWith("/grupo4")) {
                        String[] direct2 = mensaje.split(" ");
                        grupo4.forEach(
                                (key, value)
                                        -> {
                                    try {
                                        PrintWriter out = new PrintWriter(value.getOutputStream(), true);
                                        StringBuilder msg = new StringBuilder();
                                        for (int i = 1; i < direct2.length; i++) {
                                            msg.append(direct2[i]);
                                            msg.append(" ");
                                        }
                                        out.println(nombre + ": " + msg);
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                        );
                    } else if (mensaje.startsWith("/grupo5")) {
                        String[] direct2 = mensaje.split(" ");
                        grupo5.forEach(
                                (key, value)
                                        -> {
                                    try {
                                        PrintWriter out = new PrintWriter(value.getOutputStream(), true);
                                        StringBuilder msg = new StringBuilder();
                                        for (int i = 1; i < direct2.length; i++) {
                                            msg.append(direct2[i]);
                                            msg.append(" ");
                                        }
                                        out.println(nombre + ": " + msg);
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                        );
                    } else if (mensaje.startsWith("/exit")) {
                        String[] direct2 = mensaje.split(" ");
                        for (HashMap<String, Socket> grupo : listaGrupos) {
                            if (direct2[1].equals("grupo1")) {
                                grupo.remove(nombre);
                                enviarMensaje("Saliste del grupo");
                            } else if (direct2[1].equals("grupo2")) {
                                grupo.remove(nombre);
                                enviarMensaje("Saliste del grupo");
                            } else if (direct2[1].equals("grupo3")) {
                                grupo.remove(nombre);
                                enviarMensaje("Saliste del grupo");
                            } else if (direct2[1].equals("grupo4")) {
                                grupo.remove(nombre);
                                enviarMensaje("Saliste del grupo");
                            } else if (direct2[1].equals("grupo5")) {
                                grupo.remove(nombre);
                                enviarMensaje("Saliste del grupo");
                            } else {
                                enviarMensaje("Grupo invalido");
                            }
                        }
                    } else {
                        broadcast(nombre + ": " + mensaje);
                    }
                }
            } catch (IOException e) {
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