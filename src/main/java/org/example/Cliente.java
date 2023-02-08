package org.example;

import java.io.*;
import java.net.Socket;

public class Cliente implements Runnable {
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private boolean done;

    @Override
    public void run() {
        try {
            client = new Socket("localhost", 6000);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            InputHandler inHandler = new InputHandler();
            Thread t = new Thread(inHandler);
            t.start();

            String inMensaje;
            while ((inMensaje = in.readLine()) != null) {
                System.out.println(inMensaje);
            }
        } catch (IOException e) {
            shudown();
        }
    }

    public void shudown() {
        done = true;
        try {
            in.close();
            out.close();
            if (!client.isClosed()) {
                client.close();
            }
        }catch (IOException e) {
            //ignore
        }
    }

    class InputHandler implements Runnable {

        @Override
        public void run() {
            try {
                BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
                while (!done) {
                    String mensaje = inReader.readLine();
                    if (mensaje.equals("/quit")) {
                        out.println(mensaje);
                        inReader.close();
                        shudown();
                    } else {
                        out.println(mensaje);
                    }
                }
            } catch (IOException e) {
                shudown();
            }
        }
    }

    public static void main(String[] args) {
        Cliente client = new Cliente();
        client.run();
    }
}
