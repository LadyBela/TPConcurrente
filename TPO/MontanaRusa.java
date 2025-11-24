package TPO;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

public class MontanaRusa {

    private int capacidad = 5;
    private int capacidadEspera = 20;
    private int fichas = 10;
    private Semaphore espacioEspera;
    private CyclicBarrier barrera;
    private Semaphore semEsperando = new Semaphore(1); // Semaforo para la variable esperando
    private int esperando = 0;

    public MontanaRusa() {
        this.espacioEspera = new Semaphore(capacidadEspera);
        this.barrera = new CyclicBarrier(capacidad);
    }

    public void entregarFichas(Persona p) {
        p.agregarFichas("MR", fichas);
    }

    public boolean intentarSubir(Persona p) throws InterruptedException {
        boolean subio = false;
        if (!espacioEspera.tryAcquire()) {
            // Si no pudo entrar a la fila de espera se va a ir
            System.out.println(
                    "Persona " + p.getNombre() +
                            " no pudo entrar a la fila de espera de la Montaña Rusa y se va a ir");
        } else {
            // Entra en la fila de espera y ahora debe esperar a que sean 5 para arrancar
            semEsperando.acquire();
            esperando++;
            System.out.println("Persona " + p.getNombre() + " esta esperando en la Montaña Rusa (" + esperando + "/5)");

            if (esperando == capacidad) {
                // Si ya son 5, arranca el viaje
                esperando = 0;
                semEsperando.release();
            } else {
                // Si no son 5, espera a que termine el viaje
                semEsperando.release();
            }
            subio = true;
            try {
                // Van a esperar a que todos se suban
                barrera.await();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
        return subio;
    }

    public void intentarBajar(Persona p) throws InterruptedException {
        // Cuando termina el viaje, se les entrega las fichas a cada persona
        System.out.println("Se terminó el viaje de la Montaña Rusa para la persona " + p.getNombre()
                + " y se entregaran (+" + fichas + " fichas)");
        entregarFichas(p);
        // Sueltan el lugar en la fila de espera
        espacioEspera.release();
    }

}
