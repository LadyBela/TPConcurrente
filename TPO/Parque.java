package TPO;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Parque {

    private AtomicInteger molinetes;

    public MontanaRusa montanaRusa;
    public AutitosChocadores autitosChocadores;
    // public Molinetes molinetes;
    public RealidadVirtual realidadVirtual;
    public CarreraGomones carreraGomones;
    public AreaPremios areaPremios;
    public Comedor comedor;
    public Teatro teatro;
    private int hora;
    private boolean abierto;
    private Lock horario;
    private Condition esperar;

    public Parque(int cantMolinetes, int h) {
        this.montanaRusa = new MontanaRusa();
        this.autitosChocadores = new AutitosChocadores();
        this.realidadVirtual = new RealidadVirtual(10, 20, 10);
        this.carreraGomones = new CarreraGomones(15, 10, 8, 8, 12);
        this.areaPremios = new AreaPremios();
        this.comedor = new Comedor();
        this.teatro = new Teatro();

        this.molinetes = new AtomicInteger(cantMolinetes);
        this.horario = new ReentrantLock();
        this.esperar = horario.newCondition();

        this.hora = h;
        this.abierto = false;
    }

    public void entrarParque() {
        horario.lock();
        try {
            while (devolverHora() < 9 || devolverHora() > 18) {
                esperar.await();
            }
        } catch (InterruptedException ex) {
            System.out.println(ex);
        } finally {
            horario.unlock();
        }

    }

    public MontanaRusa getMontanaRusa() {
        return montanaRusa;
    }

    public AutitosChocadores getAutitosChocadores() {
        return autitosChocadores;
    }

    public void esperarMolinete() {
        molinetes.getAndDecrement();
    }

    public void dejarMolinete() {
        molinetes.getAndIncrement();
    }

    public RealidadVirtual getRealidadVirtual() {
        return realidadVirtual;
    }

    public CarreraGomones getCarreraGomones() {
        return carreraGomones;
    }

    public AreaPremios getAreaPremios() {
        return areaPremios;
    }

    public Comedor getComedor() {
        return comedor;
    }

    public Teatro getTeatro() {
        return teatro;
    }

    public int devolverHora() {
        return this.hora;
    }

    public void notificarHora(int hora, int minutos) {
        try {
            horario.lock();
            this.hora = hora;
            switch (hora) {
                case 9:
                    if (minutos == 0) {
                        System.out.println("PQ | El parque abrió sus puertas");
                        this.abierto = true;
                    }
                    break;
                case 18:
                    if (minutos == 0) {
                        System.out.println("PQ | El parque cerró sus puertas");
                        this.abierto = false;
                    }
                    break;
                case 19:
                    if (minutos == 0)
                        System.out.println("PQ | Las actividades cerraron");
                    break;
            }
            esperar.signalAll();
        } finally {
            horario.unlock();
        }
    }

    public void hacerActividad(int cantActividades, Persona p) {
        Random rand = new Random();
        try {
            boolean subio = false;
            for (int i = 0; i < cantActividades; i++) {
                int actividad = rand.nextInt(5);
                // Si cerro quiero que termine la actividad y no haga nada mas
                if (!abierto) {
                    i = cantActividades;
                    actividad = -1;
                }
                switch (actividad) {
                    case 0:
                        subio = this.getMontanaRusa().intentarSubir(p);
                        Thread.sleep(3000);
                        if (subio)
                            this.getMontanaRusa().intentarBajar(p);
                        break;
                    case 1:
                        subio = this.getAutitosChocadores().intentarSubir(p);
                        Thread.sleep(2000);
                        if (subio)
                            this.getAutitosChocadores().intentarBajar(p);
                        break;
                    case 2:
                        this.getRealidadVirtual().intentarParticipar(p);
                        Thread.sleep(2000);
                        if (p.devolverEquipo() != null) {
                            this.getRealidadVirtual().dejarDeParticipar(p);
                        }
                        break;
                    case 3:
                        // parque.getCarreraGomones().participar(this);
                        int eleccion = this.getCarreraGomones().irAlRio(p);
                        if (eleccion == 0) // Tarda mas en bici que en tren
                            Thread.sleep(2000);
                        else
                            Thread.sleep(1000);
                        this.getCarreraGomones().llegarAlRio(p, eleccion);
                        break;
                    case 4:
                        this.getComedor().sentarseEnMesa(p);
                        if (p.getMesaAsignada() != -1) {
                            this.getComedor().pedirComida(p);
                            Thread.sleep(2000);
                            this.getComedor().irseDeMesa(p);
                        }
                        break;
                    case -1:
                        break;
                }
            }
            this.areaPremios.canjearFichas(p);
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
