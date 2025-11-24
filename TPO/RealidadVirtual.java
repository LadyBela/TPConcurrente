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

    public boolean intentarParticipar(Persona p) throws InterruptedException {
        boolean participo = false;
        pedirEquipo(p);
        return participo;
    }

    public boolean dejarDeParticipar(Persona p) throws InterruptedException {
        boolean participo = false;
        devolverEquipo(p);
        return participo;
    }

    public void pedirEquipo() throws InterruptedException {

    }

    public HashMap<String, String> pedirEquipo(Persona p) {
        HashMap<String, String> equipo = new HashMap<>();
        try {
            lock.lock();
            // Avisa al cocinero
            entregarEquipo.signal();

            if (equipoListo.isEmpty()) {
                // Espera a que el equipo esté listo
                esperarEquipo.await();
            }
            equipo = equipoListo;
        } catch (Exception e) {
        } finally {
            lock.unlock();
        }
        return equipo;
    }

    public void entregarEquipo() {
        try {
            lock.lock();
            if (personasEsperando == 0) {
                entregarEquipo.await();
            }
            // Intentara agarrar el equipo
            String visor = visores.take();
            String manopla1 = manoplas.take();
            String manopla2 = manoplas.take();
            String base = bases.take();
            // Entrega el equipo
            this.equipoListo.put("visor", visor);
            this.equipoListo.put("manopla1", manopla1);
            this.equipoListo.put("manopla2", manopla2);
            this.equipoListo.put("base", base);
            esperarEquipo.signal();
            personasEsperando--;
        } catch (InterruptedException e) {
        } finally {
            lock.unlock();
        }
    }

    public void devolverEquipo(Persona p) {
        try {
            lock.lock();
            // Avisa al cocinero
            entregarEquipo.signal();
            personasEsperando++;
            // Espera
            esperarEquipo.await();
        } catch (Exception e) {
        } finally {
            lock.unlock();
        }
    }

    public void guardarEquipo() {
        try {
            lock.lock();
            if (personasEsperando == 0) {
                entregarEquipo.await();
            }
            // Hay cliente esperando
            Thread.sleep(5000);
            // Entrega la comida
            esperarEquipo.signal();
            personasEsperando--;
        } catch (InterruptedException e) {
        } finally {
            lock.unlock();
        }
    }
}
