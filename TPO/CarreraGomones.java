package TPO;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CarreraGomones {

    private boolean hayLugar;
    private AtomicInteger gomonesSolos;
    private AtomicInteger gomonesDuos;
    private AtomicInteger participantes;
    private AtomicInteger maximoCarrera;

    private Lock carrera;
    private Lock entrada;
    private Lock casillero;
    private Lock condiciones;
    private Lock horario;
    private Condition compañero;
    private Condition finCarrera;
    private Condition lugar;
    private Condition empiezaCarrera;
    private CyclicBarrier tren;
    private Semaphore bicicleta;
    private Random random;

    public CarreraGomones(int gomonSolo, int gomonDuo, int max, int bicis, int tren) {
        this.gomonesSolos = new AtomicInteger(gomonSolo);
        this.gomonesDuos = new AtomicInteger(gomonDuo);
        this.participantes = new AtomicInteger(0);
        this.maximoCarrera = new AtomicInteger(max);
    }

    public int irAlRio(Persona p) {
        int eleccion = random.nextInt(2);
        switch (eleccion) {
            // 0 es bicis , 1 es tren
            case 0:
                try {
                    System.out.println("Persona " + p.getNombre() + " va a ir al río en bicicleta");
                    bicicleta.acquire();
                } catch (InterruptedException ex) {
                    System.out.println(ex);
                }
                break;

            case 1:
                try {
                    System.out.println("Persona " + p.getNombre() + " va a ir al río en tren");
                    tren.await(8, TimeUnit.SECONDS);
                } catch (TimeoutException ex) {
                    tren.reset();
                } catch (InterruptedException | BrokenBarrierException ex) {
                }
                break;

            default:
                break;
        }
        return eleccion;
    }

    public void llegarAlRio(Persona p, int eleccion) {
        switch (eleccion) {
            // 0 es bicis , 1 es tren
            case 0:
                System.out.println("Persona " + p.getNombre() + " llego al río y va a dejar la bicicleta");
                bicicleta.release();
                break;
            case 1:
                System.out.println("Persona " + p.getNombre() + " llego al río luego de ir en tren");
                break;

            default:
                break;
        }
        anotarseEnCarrera(p);
    }

    public void anotarseEnCarrera(Persona p) {
        entrada.lock();
        int eleccion = random.nextInt(2);
        // 0 es solo , 1 es duo

        // Si no hay lugar o si no quedan mas del gomon que se quiere, va a esperar
        while (!hayLugar
                || (eleccion == 0 && gomonesSolos.get() == 0)
                || (eleccion == 1 && gomonesDuos.get() == 0)) {
            if (!hayLugar) {
                try {
                    lugar.await();
                } catch (InterruptedException ex) {
                    System.out.println();
                }
            }
            eleccion = random.nextInt(2);
        }

        participantes.getAndIncrement();
        if (participantes.get() == maximoCarrera.get()) {
            hayLugar = false;
        }
        if (eleccion == 0) {
            gomonesSolos.getAndIncrement();
        } else {
            gomonesDuos.getAndIncrement();
        }

        guardarBolsa(p);

        try {

        } finally {
            entrada.unlock();
        }

        subirseALosGomones(p, eleccion);

    }
    /*
     * public void irAlRio(Persona p) {
     * 
     * switch (random.nextInt(2)) {
     * // 0-> Bicis | 1 -> Tren
     * case 0:
     * try {
     * bicicleta.acquire();
     * Thread.sleep((int) (Math.random() * 800));
     * bicicleta.release();
     * } catch (InterruptedException ex) {
     * System.out.println(ex);
     * }
     * break;
     * 
     * case 1:
     * 
     * try {
     * tren.await(8, TimeUnit.SECONDS);
     * } catch (TimeoutException ex) {
     * tren.reset();
     * } catch (InterruptedException | BrokenBarrierException ex) {
     * }
     * 
     * try {
     * Thread.sleep((int) (Math.random() * 500));
     * } catch (InterruptedException ex) {
     * System.out.println(ex);
     * }
     * 
     * break;
     * 
     * default:
     * break;
     * }
     * anotarseEnCarrera(p);
     * }
     * 
     * public void guardarBolsa(Persona p) {
     * 
     * casillero.lock();
     * try {
     * casilleros[punteroCasillero] = p.getID();
     * punteroCasillero++;
     * } finally {
     * casillero.unlock();
     * }
     * 
     * }
     * 
     * public void retirarBolsa(Persona p) {
     * casillero.lock();
     * for (int i = 0; i < casilleros.length; i++) {
     * if (casilleros[i] == p.getID()) {
     * i = casilleros.length + 1;
     * }
     * }
     * 
     * punteroCasillero--;
     * try {
     * 
     * } finally {
     * casillero.unlock();
     * }
     * }
     * 
     * public void anotarseEnCarrera(Persona p) {
     * entrada.lock();
     * int eleccion = random.nextInt(2);
     * 
     * while (!hayLugar || (eleccion == 0 && usandoSolo == gomonSolo)
     * || (eleccion == 1 && usandoDuo == gomonDuo)) {
     * if (!hayLugar) {
     * try {
     * lugar.await();
     * } catch (InterruptedException ex) {
     * System.out.println();
     * }
     * }
     * // Si se queda con su misma eleccion puede entrar en deadlock
     * eleccion = random.nextInt(2);
     * }
     * 
     * System.out.println("lockeara Horario");
     * horario.lock();
     * if (hora >= 17) {
     * try {
     * 
     * } finally {
     * horario.unlock();
     * entrada.unlock();
     * }
     * salir(p);
     * } else {
     * try {
     * 
     * } finally {
     * horario.unlock();
     * }
     * 
     * participantes++;
     * if (participantes == maximoCarrera) {
     * hayLugar = false;
     * }
     * if (eleccion == 0) {
     * usandoSolo++;
     * } else {
     * usandoDuo++;
     * }
     * 
     * guardarBolsa(p);
     * 
     * try {
     * 
     * } finally {
     * entrada.unlock();
     * }
     * 
     * subirseALosGomones(p, eleccion);
     * }
     * }
     * 
     * public void subirseALosGomones(Persona p, int eleccion) {
     * switch (eleccion) {
     * // 0-> solo | 1 -> de a dos
     * case 0:
     * irCarrera(p);
     * break;
     * case 1:
     * carrera.lock();
     * if (!esperanPareja) {
     * buscaPareja = p.getID();
     * esperanPareja = true;
     * entrada.lock();
     * try {
     * participantes--;
     * usandoDuo--;
     * if (participantes < maximoCarrera) {
     * hayLugar = true;
     * }
     * lugar.signal();
     * 
     * } finally {
     * entrada.unlock();
     * }
     * 
     * try {
     * while (esperanPareja) {
     * compañero.await();
     * }
     * horario.lock();
     * if (hora >= 18) {
     * p.setPareja(0);
     * }
     * try {
     * 
     * } finally {
     * horario.unlock();
     * }
     * while (!carreraTerminada) {
     * finCarrera.await();
     * }
     * } catch (InterruptedException ex) {
     * 
     * } finally {
     * carrera.unlock();
     * }
     * 
     * } else {
     * try {
     * p.setPareja(buscaPareja);
     * esperanPareja = false;
     * compañero.signalAll();
     * } finally {
     * carrera.unlock();
     * }
     * irCarrera(p);
     * }
     * break;
     * default:
     * break;
     * }
     * terminarCarrera(p);
     * }
     * 
     * public void terminarCarrera(Persona p) {
     * retirarBolsa(p);
     * if (p.getIDPareja() == -1) {
     * entrada.lock();
     * try {
     * participantes--;
     * if (participantes == 0) {
     * carreraTerminada = false;
     * hayLugar = true;
     * lugar.signalAll();
     * }
     * } finally {
     * entrada.unlock();
     * }
     * } else {
     * p.setPareja(-1);
     * }
     * 
     * salir(p);
     * 
     * }
     * 
     * public void salir(Persona p) {
     * System.out.println("Persona " + p.getID() + " está saliendo del río.");
     * }
     * 
     * public void irCarrera(Persona p) {
     * condiciones.lock();
     * enCarrera++;
     * if (enCarrera == maximoCarrera) {
     * puedenComenzar = true;
     * empiezaCarrera.signalAll();
     * }
     * while (!puedenComenzar) {
     * try {
     * empiezaCarrera.await();
     * } catch (InterruptedException ex) {
     * System.out.println(ex);
     * }
     * }
     * try {
     * 
     * } finally {
     * condiciones.unlock();
     * }
     * 
     * try {
     * Thread.sleep((int) (Math.random() * 500));
     * } catch (InterruptedException ex) {
     * Logger.getLogger(CarreraGomones.class.getName()).log(Level.SEVERE, null, ex);
     * }
     * try {
     * entrada.lock();
     * if (p.getIDPareja() == -1) {
     * usandoSolo--;
     * } else {
     * usandoDuo--;
     * }
     * } finally {
     * entrada.unlock();
     * }
     * carrera.lock();
     * try {
     * terminaron++;
     * condiciones.lock();
     * if (terminaron == enCarrera) {
     * puedenComenzar = false;
     * enCarrera = 0;
     * terminaron = 0;
     * carreraTerminada = true;
     * finCarrera.signalAll();
     * }
     * try {
     * 
     * } finally {
     * condiciones.unlock();
     * }
     * } finally {
     * carrera.unlock();
     * }
     * 
     * }
     */
}
