package TPO;

public class Main {
    private static Persona[] personas = new Persona[20];
    private static Reloj reloj;
    private static Empleado encargado;
    private static Empleado cocinero;

    public static void main(String[] args) {
        Parque parque = new Parque(5);

        reloj = new Reloj();
        reloj.start();
        encargado = new Empleado(parque, "Encargado");
        encargado.start();
        cocinero = new Empleado(parque, "Cocinero");
        cocinero.start();

        for (int p = 0; p < personas.length; p++) {
            personas[p] = new Persona(p + 1, parque);
            personas[p].start();
        }

    }

}