import sun.misc.Lock;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

class Ghiseu extends Thread{

    //am considerat ca odata ce un client nu o gaseste pe caserita el va ramane la aceeasi coada si va incerca pana o gaseste.

    private int clientiTotali=0; //reprezinta cati clienti au fost pana acuma ca sa se stie exact ce numar de ordine va avea urmatorul client
    private Lock lock = new Lock();//doar un singur client poate la un momentdat sa verifice daca el este urmatorul si sa actioneze
    private Lock setDocumentLock=new Lock(); //protejeaza scrierea/citirea conditiei de finalizare a unui client
    private Lock lockFlagPauza=new Lock();
    private volatile Queue<Thread> queue=new LinkedList<>();
    public volatile boolean flagPauza=false; //grija sa verificati cu lockuri daca flagul se poate citi;false=nu e in pauza; true=e in pauza

    public Lock getLockPauza(){
        return lockFlagPauza;
    }

    public void serveste(Persoana ps){

        try {
            lock.lock();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        queue.add(ps);
            try {
                clientiTotali++;
                ps.setNumarordine(clientiTotali);
                ps.intraInCoada();
                System.out.println("Thread " +
                        Thread.currentThread().getId() +
                        " a intrat in coada  clientii  " + clientiTotali);
                lock.unlock();

                synchronized (ps) {
                    ps.wait();
                }

                System.out.println("Thread " +
                        Thread.currentThread().getId() +
                        " is running" + "    clientii totali       " + clientiTotali + "   cu numarul de ordine   " + ps.getNumarordine());

                setDocumentLock.lock();
                ps.setDocumentObtinut();
                setDocumentLock.unlock();


            } catch (InterruptedException e) {
                e.printStackTrace();
            }


    }

    // functia asta are rolul de a genera numere random pentru a face sistemul mai dinamic si odata ce se genereaza un anumit numar
    //caserita intra in pauza pentru 10 milisecunde(nu e relevant cat se asteapta ci sa functioneze).
    public void run(){

        while(true){
            Random rand = new Random();
            int  n = rand.nextInt(100000) + 1;
            if(n%5==0){

                    System.out.println("caserita a intrat in pauza");
                try {
                    Thread.sleep(10);
                    lockFlagPauza.lock();
                    flagPauza=true;
                    lockFlagPauza.unlock();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("caserita a iesit din pauza");

            }
            else{
                try {
                    lock.lock();
                    lockFlagPauza.lock();
                    flagPauza=false;
                    lockFlagPauza.unlock();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Thread client=queue.poll();
                if(client!=null){

                    while(true){

                        try {
                            setDocumentLock.lock();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if(((Persoana)client).esteDocumentulObtinut()==false) {

                            synchronized (client) {
                                client.notifyAll();
                                setDocumentLock.unlock();
                            }
                        }
                        else {
                            setDocumentLock.unlock();
                            break;
                        }


                    }
                }
                lock.unlock();
            }
        }
    }

}

public class Simulare {

    public static void main(String []argv){
//        int n = 40; // Number people
//        Ghiseu ghis=new Ghiseu();
//        ghis.start();
//        for (int i=0; i<n; i++)
//        {
//            Persoana client = new Persoana();
//            client.setGhiseu(ghis);
//            client.start();
//        }
    }
}
