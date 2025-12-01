package TPO;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

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

    public Parque(int cantMolinetes) {
        this.montanaRusa = new MontanaRusa();
        this.autitosChocadores = new AutitosChocadores();
        this.realidadVirtual = new RealidadVirtual(10, 20, 10);
        this.carreraGomones = new CarreraGomones(15, 10, 8, 8, 12);
        this.areaPremios = new AreaPremios();
        this.comedor = new Comedor();
        this.teatro = new Teatro();

        this.molinetes = new AtomicInteger(cantMolinetes);
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

    public int getMolinetesDisponibles() {
        return molinetes.get();
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
}
