package TPO;

public class Encargado extends Thread {
    private Parque parque;
    private RealidadVirtual zonaRV;

    public Encargado(Parque p) {
        this.parque = p;
        this.zonaRV = this.parque.getRealidadVirtual();
    }

    @Override
    public void run() {
        System.out.println("Encargado inició su turno");

        while (true) {
            System.out.println(" Encargado esperando para entregar equipo de Realidad Virtual ");
            zonaRV.entregarEquipo();

            System.out.println(" Encargado esperando para guardar equipo de Realidad Virtual ");
            zonaRV.guardarEquipo();

        }
    }
}
