package TPO;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Comedor {
    private int cantidadMesas = 5;
    private int capacidadPorMesa = 4;
    private HashMap listaMesas = new HashMap(cantidadMesas);
    private HashMap listaPedidos = new HashMap(cantidadMesas);
    private HashMap comidaLista = new HashMap(cantidadMesas);

    private Lock lock = new ReentrantLock(true);
    private Condition esperarComida = lock.newCondition();
    private Condition prepararComida = lock.newCondition();

    public Comedor() {
        for (int i = 0; i < cantidadMesas; i++) {
            CountDownLatch mesa = new CountDownLatch(capacidadPorMesa);
            CountDownLatch pedido = new CountDownLatch(capacidadPorMesa);
            listaMesas.put("mesa" + i, mesa);
            listaPedidos.put("pedido" + i, pedido);
            comidaLista.put("comida" + i, false);
        }
    }

    public void sentarseEnMesa(Persona p) {
        // Si no hay mesa disponible, se va
        for (int i = 0; i < cantidadMesas; i++) {
            CountDownLatch mesa = (CountDownLatch) listaMesas.get("mesa" + i);

            if (mesa.getCount() > 0) {
                // Si la mesa tiene lugar intentará sentarse, sino intentará pasar a la siguiente
                try {
                    mesa.countDown();
                    p.asignarMesa(i);
                    System.out.println(" Persona " + p.getNombre() + " se sentó en la mesa " + i);
                    mesa.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void pedirComida(Persona p) {
        int numMesa = p.getMesaAsignada();
        CountDownLatch pedidoMesa = (CountDownLatch) listaPedidos.get("pedido" + numMesa);

        try {
            System.out.println(" Persona " + p.getNombre() + " está pidiendo comida en la mesa " + numMesa);
            pedidoMesa.countDown();
            pedidoMesa.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        lock.lock();
        try {
            prepararComida.signalAll();

            while (!((boolean) comidaLista.get("comida" + numMesa))) {
                esperarComida.await();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

        System.out.println(" Persona " + p.getNombre() + " recibió su comida.");

    }

    public void irseDeMesa(Persona p) {
        int mesa = p.getMesaAsignada();
        System.out.println("Persona " + p.getNombre() + " se retira de la mesa " + mesa);

        CountDownLatch pedidoMesa = (CountDownLatch) listaPedidos.get("pedido" + mesa);
        pedidoMesa.countDown();
        if (pedidoMesa.getCount() == 0) {

            lock.lock();
            try {
                //Reinicio las mesas, pedidos y la comida
                listaMesas.put("mesa" + mesa, new CountDownLatch(capacidadPorMesa));
                listaPedidos.put("pedido" + mesa, new CountDownLatch(capacidadPorMesa));
                comidaLista.put("comida" + mesa, false);

                System.out.println(" La mesa " + mesa + " se acaba de vaciar.");
            } finally {
                lock.unlock();
            }
        }

    }

    public void entregarComida() {
        lock.lock();
        try {
            prepararComida.await();

            for (int i = 0; i < cantidadMesas; i++) {
                CountDownLatch pedidoMesa = (CountDownLatch) listaPedidos.get("pedido" + i);
                System.out.println("pedido mesa " + i + " count " + pedidoMesa.getCount());
                if (pedidoMesa.getCount() == 0) {
                    //pedidoMesa.getCount() es la cantidad restante de pedidos a hacer en esa mesa, tiene que ser 0
                    System.out.println(" Hay pedido para la mesa " + i);
                    Thread.sleep(2000);
                    System.out.println(" Comida lista para la mesa " + i);
                    comidaLista.put("comida" + i, true);
                    pedidoMesa = new CountDownLatch(capacidadPorMesa);
                    listaPedidos.put("pedido" + i, pedidoMesa);
                    esperarComida.signalAll(); 
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
