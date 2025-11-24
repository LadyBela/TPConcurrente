package TPO;

public class Main {
    private static Persona[] personas = new Persona[80];
    private static Reloj reloj;
    private static Encargado encargado;

    public static void main(String[] args) {
        Parque parque = new Parque(5);

        reloj = new Reloj();
        reloj.start();
        encargado = new Encargado(parque);
        encargado.start();
        
        for (int p = 0; p < personas.length; p++) {
            personas[p] = new Persona(p + 1, parque);
            personas[p].start();
        }

    }

}