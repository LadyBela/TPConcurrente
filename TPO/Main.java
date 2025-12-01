package TPO;

public class Main {
    private static Persona[] personas = new Persona[50];
    private static Reloj reloj;
    private static Empleado encargado;
    private static Empleado cocinero;

    public static void main(String[] args) {
        Parque parque = new Parque(5, 8);
        reloj = new Reloj(parque);
        reloj.start();

        encargado = new Empleado(parque, "Encargado");
        cocinero = new Empleado(parque, "Cocinero");
        encargado.start();
        cocinero.start();

        // System.out.println("despues de iniciar encargado");

        for (int p = 0; p < personas.length; p++) {
            personas[p] = new Persona(p + 1, parque);
            personas[p].start();
        }

    }

}