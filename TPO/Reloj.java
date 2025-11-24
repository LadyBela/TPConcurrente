package TPO;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Reloj extends Thread {
    private static int hora = 12;
    private static int minutos = 0;
    // private static final Object lock = new Object();

    @Override
    public void run() {
        try {
            while (true) {
                // 500 ms corresponden a 1 minuto del parque
                Thread.sleep(50);
                minutos += 1;
                if (minutos > 59) {
                    hora++;
                    minutos = 0;
                    System.out.println(" ---> Hora actual del parque: " + String.format("%02d:%02d", hora, minutos));
                    if (hora > 23) {
                        hora = 0;
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Obtiene la hora actual en formato HH:MM

    public String getHoraActual() {
        String hora = "";
        try {
            hora = String.format("%02d:%02d", hora, minutos);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return hora;
    }

}