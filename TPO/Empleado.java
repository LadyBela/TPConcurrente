package TPO;

public class Empleado extends Thread {
    private Parque parque;
    private RealidadVirtual zonaRV;
    private Comedor zonaComedor;
    private String tipoEmpleado;

    public Empleado(Parque p, String tipo) {
        this.parque = p;
        this.zonaRV = this.parque.getRealidadVirtual();
        this.zonaComedor = this.parque.getComedor();
        this.tipoEmpleado = tipo;
    }

    @Override
    public void run() {
        // Ver hora parque
        while (true) {
            if (tipoEmpleado.equals("Encargado")) {
                System.out.println(this.tipoEmpleado + " inició su turno");
                tareasEncargado();
            }

            if (tipoEmpleado.equals("Cocinero")) {
                System.out.println(this.tipoEmpleado + " inició su turno");
                tareasCocinero();
            }
        }
    }

    private void tareasEncargado() {

        System.out.println(" Encargado esperando para entregar equipo de Realidad Virtual ");
        zonaRV.entregarEquipo();

        System.out.println(" Encargado esperando para guardar equipo de Realidad Virtual ");
        zonaRV.guardarEquipo();

    }

    private void tareasCocinero() {
        System.out.println(" Cocinero va a ver si hay pedidos de comida ");
        zonaComedor.entregarComida();
    }

}
