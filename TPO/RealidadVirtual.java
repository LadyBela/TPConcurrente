package TPO;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RealidadVirtual {

    private BlockingQueue<String> visores = new LinkedBlockingQueue<>();
    private BlockingQueue<String> manoplas = new LinkedBlockingQueue<>();
    private BlockingQueue<String> bases = new LinkedBlockingQueue<>();

    private int personasEsperando = 0;
    private Lock lock = new ReentrantLock(true);
    private Condition esperarEquipo = lock.newCondition();
    private Condition entregarEquipo = lock.newCondition();
    private Condition devolverEquipo = lock.newCondition();
    private HashMap<String, String> equipoListo = new HashMap<>();
    private HashMap<String, String> equipoEntregado = new HashMap<>();
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

    public HashMap<String, String> intentarParticipar(Persona p) throws InterruptedException {
        System.out.println(
                "La persona " + p.getNombre() + " va a participar de la Realidad Virtual y va a obtener equipo.");
        HashMap<String, String> equipo = pedirEquipo(p);
        System.out.println(
                "La persona " + p.getNombre() + " ya tiene su equipo." + equipo);
        return equipo;
    }

    public void dejarDeParticipar(Persona p, HashMap<String, String> equipo) throws InterruptedException {
        System.out.println("  " + p.getNombre() + " va a devolver equipo de Realidad Virtual ");
        devolverEquipo(equipo);
        System.out.println("Se terminó la Realidad Virtual para la persona " + p.getNombre()
                + " y se entregaran (+" + fichas + " fichas)");
        entregarFichas(p);
    }

    public HashMap<String, String> pedirEquipo(Persona p) {
        HashMap<String, String> equipo = new HashMap<>();
        try {
            lock.lock();
            personasEsperando++;
            while (equipoListo.isEmpty()) {
                // Mientras el equipo no esté listo
                entregarEquipo.signal();
                esperarEquipo.await();
            }
            // Agarra el equipo y se va
            equipo = equipoListo;
            equipoListo = new HashMap<>();

        } catch (Exception e) {
        } finally {
            lock.unlock();
        }
        return equipo;
    }

    public void entregarEquipo() {
        try {
            lock.lock();
            entregarEquipo.await();
            if (personasEsperando != 0) {
                // Intentara preparar el equipo
                    String visor = visores.take();
                    String manopla1 = manoplas.take();
                    String manopla2 = manoplas.take();
                    String base = bases.take();
                    // Entrega el equipo
                    this.equipoListo.put("visor", visor);
                    this.equipoListo.put("manopla1", manopla1);
                    this.equipoListo.put("manopla2", manopla2);
                    this.equipoListo.put("base", base);
                    System.out.println(" Encargado entregó  equipo de Realidad Virtual "+equipoListo);
                    personasEsperando--;

            }
            esperarEquipo.signal();
        } catch (InterruptedException e) {
        } finally {
            lock.unlock();
        }
    }

    public void devolverEquipo(HashMap<String, String> equipo) {
        try {
            lock.lock();
            // Devuelve el equipo
            this.equipoEntregado.putAll(equipo);
            while (!equipoListo.isEmpty()) {
                // Mientras el equipo no esté listo
                entregarEquipo.signal();
                devolverEquipo.await();
            }
            esperarEquipo.signalAll();  //Le avisa a la gente que ya hay algun equipo disponible
        } catch (Exception e) {
        } finally {
            lock.unlock();
        }
    }

    public void guardarEquipo() {
        try {
            lock.lock();
            entregarEquipo.await();
            if (!equipoEntregado.isEmpty()) {
                // Vuelvo a guardar el equipo
                visores.put(equipoEntregado.get("visor"));
                manoplas.put(equipoEntregado.get("manopla1"));
                manoplas.put(equipoEntregado.get("manopla2"));
                bases.put(equipoEntregado.get("base"));
                System.out.println(" Encargado guardó el equipo de Realidad Virtual ");
                // Aviso para que se vaya la persona
                equipoEntregado = new HashMap<>();
                devolverEquipo.signal();
            }
        } catch (InterruptedException e) {
        } finally {
            lock.unlock();
        }
    }
}
