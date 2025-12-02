package TPO;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class Teatro {

    private final int capacidad = 20;
    private final int tamGrupo = 5;

    private Semaphore cupoTeatro = new Semaphore(capacidad);

    private CyclicBarrier barreraGrupo;

    private AtomicInteger totalDentro;

    public Teatro() {
        this.barreraGrupo = new CyclicBarrier(tamGrupo);
        this.totalDentro = new AtomicInteger(0);
    }

    public boolean intentarEntrar(Persona p) throws InterruptedException {
        boolean entrar = true;
        // Va a intentar entrar al teatro, si está lleno se va
        if (!cupoTeatro.tryAcquire()) {
            System.out.println("T | Persona " + p.getNombre() + " no pudo entrar al teatro, está lleno.");
            entrar = false;
        } else {
            entrar = true;
            try {
                // Entra en la fila de espera y ahora debe esperar a que sean 5 para arrancar
                System.out.println("T | Persona+ " + p.getNombre() + " llegó al grupo de entrada");
                barreraGrupo.await();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }

            int totaltotalDentro = totalDentro.incrementAndGet();
            System.out.println(
                    "T | Persona+ " + p.getNombre() + " entró al teatro. Total totalDentro: " + totaltotalDentro);
        }
        return entrar;
    }

    public void verObra(Persona p) {
        // Si ya hay 20 personas va a comenzar el espectáculo
        if (totalDentro.get() == capacidad) {
            System.out.println("T | Va a comenzar la obra");
            try {
                Thread.sleep(2000);
                System.out.println("T | La obra termino y las personas se van a ir.");
                totalDentro.set(0);
                cupoTeatro.release(capacidad);
                System.out.println("T | El teatro se encuentra vacio esperando para la proxima funcion.");
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}