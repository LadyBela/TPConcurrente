package TPO;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class MontanaRusa {

    private int capacidad = 5;
    private int capacidadEspera = 20;
    private int fichas = 10;
    private Semaphore espacioEspera;
    private CyclicBarrier barreraInicio;
    private CyclicBarrier barreraSalida;
    // private Semaphore semEsperandoSubir = new Semaphore(1); // Semaforo para la
    // variable esperando
    private Semaphore semInicio = new Semaphore(5);

    private AtomicInteger esperando = new AtomicInteger(0);

    public MontanaRusa() {
        this.espacioEspera = new Semaphore(capacidadEspera);
        this.barreraInicio = new CyclicBarrier(capacidad);
        this.barreraSalida = new CyclicBarrier(capacidad);
    }

    public void entregarFichas(Persona p) {
        p.agregarFichas("MR", fichas);
    }

    public boolean intentarSubir(Persona p) throws InterruptedException {
        boolean subio = false;
        if (!espacioEspera.tryAcquire()) {
            // Si no pudo entrar a la fila de espera se va a ir
            System.out.println(
                    "MR | Persona " + p.getNombre() +
                            " no pudo entrar a la fila de espera de la Montaña Rusa y se va a ir");
        } else {
            try {
                // Entra en la fila de espera y ahora debe esperar a que sean 5 para arrancar
                System.out.println("MR | Persona " + p.getNombre() + " entró en la fila de espera de la Montaña Rusa");
                semInicio.acquire();
                barreraInicio.await();
                // esperando++;
                esperando.getAndIncrement();
                System.out.println(
                        "MR | Persona " + p.getNombre() + " esta esperando a subir (" + esperando.get() + "/5)");
                subio = true;

                // Van a esperar a que todos se suban

            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
        return subio;
    }

    public void intentarBajar(Persona p) throws InterruptedException {
        // Cuando termina el viaje, se les entrega las fichas a cada persona
        System.out.println("MR | Se terminó el viaje de la Montaña Rusa para la persona " + p.getNombre()
                + " y se entregaran (+" + fichas + " fichas)");
        entregarFichas(p);
        try {
            // Hasta que no esten todos listos, no pueden bajar
            barreraSalida.await();
            esperando.getAndDecrement();
            semInicio.release();
            espacioEspera.release();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }

    }

}
