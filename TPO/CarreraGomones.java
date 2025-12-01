package TPO;

import java.util.ArrayList;
import java.util.List;
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
    private boolean esperanPareja;
    private boolean carreraTerminada;
    private boolean puedenEntrar;
    private boolean puedenComenzar;

    private AtomicInteger gomonesSolosLibres;
    private AtomicInteger gomonesDuosLibres;
    private AtomicInteger gomonesSolosOcupados;
    private AtomicInteger gomonesDuosOcupados;
    private AtomicInteger participantes;
    private AtomicInteger maximoCarrera;
    private AtomicInteger enCarrera;
    private AtomicInteger terminaron;

    private int[] casilleros;
    private List ganadores;
    private int casilleroVacio;
    private int buscanPareja;
    private int fichas = 20;

    private Lock carrera;
    private Lock entrada;
    private Lock casillero;
    private Lock condiciones;
    private Condition compañero;
    private Condition finCarrera;
    private Condition lugar;
    private Condition empiezaCarrera;
    private CyclicBarrier tren;
    private Semaphore bicicleta;
    private Random random;

    public CarreraGomones(int gomonSolo, int gomonDuo, int max, int bicis, int tren) {
        this.gomonesSolosLibres = new AtomicInteger(gomonSolo);
        this.gomonesDuosLibres = new AtomicInteger(gomonDuo);
        this.gomonesSolosOcupados = new AtomicInteger(0);
        this.gomonesDuosOcupados = new AtomicInteger(0);
        this.participantes = new AtomicInteger(0);
        this.maximoCarrera = new AtomicInteger(max);
        this.enCarrera = new AtomicInteger(0);
        this.terminaron = new AtomicInteger(0);
        this.hayLugar = true;
        this.buscanPareja = -1;
        this.carreraTerminada = false;
        this.puedenEntrar = true;
        this.puedenComenzar = false;

        this.carrera = new ReentrantLock();
        this.entrada = new ReentrantLock(true);
        this.casillero = new ReentrantLock();
        this.condiciones = new ReentrantLock();
        this.compañero = carrera.newCondition();
        this.finCarrera = carrera.newCondition();
        this.lugar = entrada.newCondition();
        this.empiezaCarrera = condiciones.newCondition();

        this.casilleroVacio = 0;
        this.casilleros = new int[max * 2];
        this.bicicleta = new Semaphore(bicis);
        this.tren = new CyclicBarrier(tren);
        this.random = new Random();
        this.ganadores = new ArrayList();
    }

    public int irAlRio(Persona p) {
        int eleccion = random.nextInt(2);
        switch (eleccion) {
            // 0 es bicis , 1 es tren
            case 0:
                try {
                    System.out.println("CG | Persona " + p.getNombre() + " va a ir al río en bicicleta");
                    bicicleta.acquire();
                } catch (InterruptedException ex) {
                    System.out.println(ex);
                }
                break;

            case 1:
                try {
                    System.out.println("CG | Persona " + p.getNombre() + " va a ir al río en tren");
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
                System.out.println("CG | Persona " + p.getNombre() + " llego al río y va a dejar la bicicleta");
                bicicleta.release();
                break;
            case 1:
                System.out.println("CG | Persona " + p.getNombre() + " llego al río luego de ir en tren");
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
                || (eleccion == 0 && gomonesSolosLibres.get() == 0)
                || (eleccion == 1 && gomonesDuosLibres.get() == 0)) {
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
            gomonesSolosLibres.getAndDecrement();
            gomonesSolosOcupados.getAndIncrement();
        } else {
            gomonesDuosLibres.getAndDecrement();
            gomonesDuosOcupados.getAndIncrement();
        }

        guardarBolsa(p);

        entrada.unlock();

        subirseALosGomones(p, eleccion);
    }

    public void guardarBolsa(Persona p) {
        casillero.lock();
        int casilleroPersona = casilleroVacio;
        System.out.println(
                "CG | Persona " + p.getNombre() + " va a guardar sus cosas en el casillero " + casilleroPersona);
        casilleros[casilleroVacio] = p.getNombre();
        p.asignarCasillero(casilleroPersona);
        casilleroVacio++;
        casillero.unlock();
    }

    public void subirseALosGomones(Persona p, int miEleccion) {
        switch (miEleccion) {
            // 0-> solo | 1 -> de a dos
            case 0:
                irCarrera(p);
                break;
            case 1:
                carrera.lock();
                if (!esperanPareja) {
                    buscanPareja = p.getNombre();
                    esperanPareja = true;
                    entrada.lock();
                    try {
                        participantes.getAndDecrement();
                        p.asignarGomon(miEleccion, gomonesDuosLibres.get(), 0);
                        gomonesDuosLibres.getAndDecrement();
                        if (participantes.get() < maximoCarrera.get()) {
                            puedenEntrar = true;
                        }
                        lugar.signal();
                    } finally {
                        entrada.unlock();
                    }
                    try {
                        while (esperanPareja) {
                            compañero.await();
                        }
                        while (!carreraTerminada) {
                            finCarrera.await();
                        }
                    } catch (InterruptedException ex) {
                    } finally {
                        carrera.unlock();
                    }
                } else {
                    try {
                        p.asignarParejaGomon(buscanPareja);
                        esperanPareja = false;
                        compañero.signalAll();
                    } finally {
                        carrera.unlock();
                    }
                    irCarrera(p);
                }
                break;
            default:
                break;
        }
        terminarCarrera(p);
    }

    public void terminarCarrera(Persona p) {
        retirarBolsa(p);
        if (((int) p.getGomonASignado().get("pareja")) == 0) {
            entrada.lock();
            try {
                participantes.getAndDecrement();
                if (participantes.get() == 0) {
                    carreraTerminada = false;
                    puedenEntrar = true;
                    lugar.signalAll();
                }
            } finally {
                entrada.unlock();
            }
        } else {
            p.asignarParejaGomon(0);

        }
        System.out.println("CG | Persona " + p.getNombre() + " Ha terminado la carrera y se va a ir");

    }

    public void retirarBolsa(Persona p) {
        casillero.lock();

        System.out.println("CG | La persona retirara su bolsa del casillero " + p.getCasilleroAsignado());

        casilleros[p.getCasilleroAsignado()] = -1;

        casilleroVacio--;

        casillero.unlock();
    }

    public void irCarrera(Persona p) {
        condiciones.lock();
        enCarrera.getAndIncrement();

        int tienepareja = ((int) p.getGomonASignado().get("pareja"));
        if (tienepareja == 0) {
            System.out.println("CG | Persona " + p.getNombre() + " esta esperando a que arranque la carrera.");
        } else {
            System.out.println(
                    "CG | Persona " + p.getNombre() + " y " + tienepareja
                            + " esta esperando a que arranque la carrera.");
            enCarrera.getAndIncrement();
        }
        if (enCarrera.get() == maximoCarrera.get()) {
            puedenComenzar = true;
            empiezaCarrera.signalAll();
        }
        while (!puedenComenzar) {
            try {
                empiezaCarrera.await();
            } catch (InterruptedException ex) {
                System.out.println(ex);
            }
        }
        condiciones.unlock();

        try {
            Thread.sleep(4000);
        } catch (InterruptedException ex) {
        }
        try {
            entrada.lock();
            if (tienepareja == 0) {
                gomonesSolosOcupados.getAndDecrement();
            } else {
                gomonesDuosOcupados.getAndDecrement();
            }
        } finally {
            entrada.unlock();
        }
        carrera.lock();

        try {
            System.out.println("CG | Persona " + p.getNombre() + "terminó la carrera.");
            if (terminaron.get() == 0) {
                terminaron.getAndIncrement();
                System.out.println("CG | Persona " + p.getNombre() + " ganó la carrera.");
                ganadores.add(p.getNombre());
                if (tienepareja != 0) {
                    System.out.println("CG | Con su pareja " + tienepareja);
                    ganadores.add(tienepareja);
                }
            } else {
                terminaron.getAndIncrement();
            }
            condiciones.lock();
            if (terminaron.get() == enCarrera.get()) {
                puedenComenzar = false;
                enCarrera.set(0);
                terminaron.set(0);
                carreraTerminada = true;
                finCarrera.signalAll();
            }
            condiciones.unlock();
        } finally {
            carrera.unlock();
        }
        for (int i = 0; i < ganadores.size(); i++) {
            if (((int) ganadores.get(i)) == p.getNombre()) {
                entregarPremios(p, i);
            }
        }
    }

    private void entregarPremios(Persona p, int posicion) {
        System.out.println("CG | Se van a entregar fichas a ganador carrera gomones: " + p.getNombre());
        p.agregarFichas("CG", fichas);
        ganadores.remove(posicion);
    }
}
