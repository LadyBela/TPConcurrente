package TPO;

import java.util.HashMap;
import java.util.Random;

public class Persona extends Thread {
    private int nombre;
    private Monedero monedero;
    private Parque parque;
    private int mesaAsignada = -1;
    private HashMap<String, String> equipo;

    public Persona(int n, Parque p) {
        this.nombre = n;
        this.monedero = new Monedero();
        this.parque = p;
    }

    public int getNombre() {
        return this.nombre;
    }

    public void asignarMesa(int mesa) {
        this.mesaAsignada = mesa;
    }

    public int getMesaAsignada() {
        return this.mesaAsignada;
    }

    public void agregarFichas(String tipo, int cantFichas) {
        this.monedero.agregarFichas(tipo, cantFichas);
    }

    public void quitarFichas(String tipo, int cantFichas) {
        this.monedero.quitarFichas(tipo, cantFichas);
    }

    public void agregarEquipo(HashMap<String, String> equipoObtenido) {
        this.equipo = equipoObtenido;
    }

    public HashMap<String, String> devolverEquipo() {
        return this.equipo;
    }





    // En el run va a tener un "subir a juego" y si retorna true, va a esperar un
    // tiempo y luego va a "bajar del juego"

    @Override
    public void run() {
        try {
            while (true) {
                // Ingresar por molinete

                Random rand = new Random();
                /*
                 * Random rand = new Random();
                 * int actividades = rand.nextInt(3) + 2;
                 */
                int actividades = 1;
                boolean subio = false;
                for (int i = 0; i < actividades; i++) {
                    // int actividad = rand.nextInt(6);
                    int actividad = 2; // pongo para ir probando de a 1 actividad fija
                    switch (actividad) {
                        case 0:
                            subio = parque.getMontanaRusa().intentarSubir(this);
                            Thread.sleep(5000);
                            if (subio)
                                parque.getMontanaRusa().intentarBajar(this);
                            break;
                        case 1:
                            subio = parque.getAutitosChocadores().intentarSubir(this);
                            Thread.sleep(2000);
                            if (subio)
                                parque.getAutitosChocadores().intentarBajar(this);
                            break;
                        case 2:
                            parque.getRealidadVirtual().intentarParticipar(this);
                            Thread.sleep(2000);
                            if (equipo != null){
                                parque.getRealidadVirtual().dejarDeParticipar(this);
                            }
                            break;
                        case 3:
                            // parque.getCarreraGomones().participar(this);
                            break;
                        case 4:
                            // parque.areaPremios.canjearFichas(this);
                            break;
                        case 5:
                            parque.getComedor().sentarseEnMesa(this);
                            if (mesaAsignada != -1) {
                                parque.getComedor().pedirComida(this);
                                Thread.sleep(3000); 
                                parque.getComedor().irseDeMesa(this);
                            }
                            // parque.comedor.intentarComer(this);
                            break;
                    }

                }
                Thread.sleep(5000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }

}
