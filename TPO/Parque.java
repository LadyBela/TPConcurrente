package TPO;

public class Parque {

    public MontanaRusa montanaRusa;
    public AutitosChocadores autitosChocadores;
    public Molinetes molinetes;
    public RealidadVirtual realidadVirtual;
    public CarreraGomones carreraGomones = new CarreraGomones();
    public AreaPremios areaPremios;
    public Comedor comedor;
    public Teatro teatro;

    public Parque(int cantMolinetes) {
        this.montanaRusa = new MontanaRusa();
        this.autitosChocadores = new AutitosChocadores();
        this.molinetes = new Molinetes(cantMolinetes);
        this.realidadVirtual = new RealidadVirtual(10, 20, 10);
        this.carreraGomones = new CarreraGomones(15, 30, 10, 8);
        this.areaPremios = new AreaPremios();
        this.comedor = new Comedor();
        this.teatro = new Teatro();
    }

    public MontanaRusa getMontanaRusa() {
        return montanaRusa;
    }

    public AutitosChocadores getAutitosChocadores() {
        return autitosChocadores;
    }

    public Molinetes getMolinetes() {
        return molinetes;
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
