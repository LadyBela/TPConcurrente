package TPO;

public class Reloj extends Thread {
    private int hora;
    private static int minutos = 0;
    private Parque parque;

    public Reloj(Parque p) {
        this.parque = p;
        this.hora = p.devolverHora();
    }

    public int getHora() {
        return this.hora;
    }

    @Override
    public void run() {
        try {
            while (true) {
                parque.notificarHora(hora, minutos);
                Thread.sleep(60);
                minutos += 1;
                if (minutos > 59) {
                    hora++;
                    minutos = 0;
                    System.out.println("R | ---------------------> Hora actual del parque: "
                            + String.format("%02d:%02d", hora, minutos));
                    if (hora > 23) {
                        hora = 0;
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}