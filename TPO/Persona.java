package TPO;

public class Persona {
    private String nombre;
    private Monedero monedero;

    public Persona(String n) {
        this.nombre = n;
        this.monedero = new Monedero(); // Cada persona empieza sin fichas
    }

    public String getNombre() {
        return this.nombre;
    }

    public void agregarFichas(String tipo, int cantFichas) {
        this.monedero.agregarFichas(tipo, cantFichas);
    }

    public void quitarFichas(String tipo, int cantFichas) {
        this.monedero.quitarFichas(tipo, cantFichas);
    }

    // En el run va a tener un "subir a juego" y si retorna true, va a esperar un
    // tiempo y luego va a "bajar del juego"
}
