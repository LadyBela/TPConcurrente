package TPO;

public class Main {
    private static Persona[] personas = new Persona[100];
    private static Reloj reloj;
    private static Empleado encargado;
    private static Empleado encargado2;
    private static Empleado cocinero;

    public static void main(String[] args) {
        Parque parque = new Parque(5, 8);
        reloj = new Reloj(parque);
        reloj.start();

        encargado = new Empleado(parque, "Encargado");
        encargado2 = new Empleado(parque, "Encargado");
        cocinero = new Empleado(parque, "Cocinero");
        encargado.start();
        encargado2.start();
        cocinero.start();

        for (int p = 0; p < personas.length; p++) {
            personas[p] = new Persona(p + 1, parque);
            personas[p].start();
        }

    }

}