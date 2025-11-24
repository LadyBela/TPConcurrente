package TPO;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

public class AutitosChocadores {

    private int cantidadAutos = 10;
    private int personasPorAuto = 2;
    private int capacidadTotal = cantidadAutos * personasPorAuto;
    private CyclicBarrier barrera;
    private Semaphore semSalida = new Semaphore(capacidadTotal);
    private int fichas = 5;

    public AutitosChocadores() {
        this.barrera = new CyclicBarrier(capacidadTotal);
    }

    public void entregarFichas(Persona p) {
        p.agregarFichas("AC", fichas);
    }

    public boolean intentarSubir(Persona p) throws InterruptedException {
        boolean subio = false;
        // No hay fila de espera, se suben directamente cuando hay lugar
        System.out.println("Persona " + p.getNombre() + " esperando Autitos Chocadores");
        try {
            if (!semSalida.tryAcquire()) {
                System.out.println("Persona " + p.getNombre() +
                        " no pudo subir a los Autitos Chocadores porque no hay lugar disponible y se va a ir");
                subio = false;
            } else {
                subio = true;
                System.out.println("Persona " + p.getNombre() + " se subió a los Autitos Chocadores");
                barrera.await();
            }
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
        return subio;
    }

    public void intentarBajar(Persona p) {
        // Cuando termina el juego, se les entrega las fichas a cada persona
        System.out.println("Se terminaron los Autitos Chocadores para la persona " + p.getNombre()
                + " y se entregaran (+" + fichas + " fichas)");
        entregarFichas(p);
        semSalida.release();
    }

}
