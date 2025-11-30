package TPO;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Exchanger;
import java.util.concurrent.LinkedBlockingQueue;

public class RealidadVirtual {

    private BlockingQueue<String> visores = new LinkedBlockingQueue<>();
    private BlockingQueue<String> manoplas = new LinkedBlockingQueue<>();
    private BlockingQueue<String> bases = new LinkedBlockingQueue<>();

    private BlockingQueue<Exchanger<HashMap>> colaPedidos = new LinkedBlockingQueue<>();

    private BlockingQueue<Exchanger<HashMap>> colaDevoluciones = new LinkedBlockingQueue<>();
    private int fichas = 12;

    public RealidadVirtual(int visores, int manoplas, int bases) {
        for (int i = 0; i < visores; i++)
            this.visores.add("Visor" + i);

        for (int i = 0; i < manoplas; i++)
            this.manoplas.add("Manopla" + i);

        for (int i = 0; i < bases; i++)
            this.bases.add("Base" + i);
    }

    public void entregarFichas(Persona p) {
        p.agregarFichas("RV", fichas);
    }

    public void intentarParticipar(Persona p) throws InterruptedException {
        System.out.println("La persona " + p.getNombre() + " va a participar de la Realidad Virtual.");
        Exchanger<HashMap> equipo = new Exchanger<>();
        colaPedidos.put(equipo);
        p.agregarEquipo(equipo.exchange(new HashMap<>()));
        System.out.println("La persona " + p.getNombre() + " ya tiene su equipo. " + p.devolverEquipo());

    }

    public void dejarDeParticipar(Persona p) throws InterruptedException {
        System.out.println("  " + p.getNombre() + " va a devolver equipo de Realidad Virtual ");
        Exchanger<HashMap> equipo = new Exchanger<>();
        colaDevoluciones.put(equipo);
        p.agregarEquipo(equipo.exchange(p.devolverEquipo()));
        System.out.println("Se terminó la Realidad Virtual para la persona " + p.getNombre()
                + " y se entregaran (+" + fichas + " fichas)");
        entregarFichas(p);
    }

    public void entregarEquipo() {

        Exchanger<HashMap> ex;
        try {
            ex = colaPedidos.take();

            HashMap equipo = new HashMap<>();
            equipo.put("visor", visores.take());
            equipo.put("manopla1", manoplas.take());
            equipo.put("manopla2", manoplas.take());
            equipo.put("base", bases.take());

            System.out.println("Encargado preparó el equipo: " + equipo);

            ex.exchange(equipo); // Lo pasa a una persona
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void guardarEquipo() {
        try {
            Exchanger<HashMap> ex = colaDevoluciones.take();
            HashMap<String, String> devuelto = ex.exchange(null);

            // Guardar las piezas
            visores.put(devuelto.get("visor"));
            manoplas.put(devuelto.get("manopla1"));
            manoplas.put(devuelto.get("manopla2"));
            bases.put(devuelto.get("base"));

            System.out.println("Encargado guardó el equipo devuelto: " + devuelto);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
