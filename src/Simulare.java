import sun.misc.Lock;

import java.util.Random;

class Ghiseu extends Thread{

    //am considerat ca odata ce un client nu o gaseste pe caserita el va ramane la aceeasi coada si va incerca pana o gaseste.

    private int clientiTotali=0; //reprezinta cati clienti au fost pana acuma ca sa se stie exact ce numar de ordine va avea urmatorul client
    private int clientCurent=0;//reprezinta clientul curent pentru a se sti cine urmeaza la coada
    private Lock lock = new Lock();//doar un singur client poate la un momentdat sa verifice daca el este urmatorul si sa actioneze
    private Lock sleepLock=new Lock();// are rolul de a bloca tot sistemul cand caserita pleaca in pauza.


    public void serveste(Persoana ps){


        try {
            sleepLock.lock();
            lock.lock();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(ps.getNumarordine()==clientCurent) {

            try {

                // Displaying the thread that is running
                System.out.println("Thread " +
                        Thread.currentThread().getId() +
                        " is running" + "    clientii totali       " + clientiTotali + "   cu numarul de ordine   "+ps.getNumarordine());

                ps.setDocumentObtinut();
            } catch (Exception e) {
                // Throwing an exception
                System.out.println("Exception is caught");
            }
            clientCurent++;
        }
        else {
            if (ps.esteInCoada() == false) {
                clientiTotali++;
                ps.setNumarordine(clientiTotali);
                ps.intraInCoada();
                System.out.println("Thread " +
                        Thread.currentThread().getId() +
                        " a intrat in coada  clientii  "+ clientiTotali);

            }
        }
        lock.unlock();
        sleepLock.unlock();
    }

    // functia asta are rolul de a genera numere random pentru a face sistemul mai dinamic si odata ce se genereaza un anumit numar
    //caserita intra in pauza pentru 10 milisecunde(nu e relevant cat se asteapta ci sa functioneze).
    public void run(){

        while(true){
            Random rand = new Random();
            int  n = rand.nextInt(100000) + 1;
            if(n==33){
                try {
                    sleepLock.lock();
                    System.out.println("caserita a intrat in pauza");
                    Thread.sleep(10);
                    System.out.println("caserita a iesit din pauza");
                    sleepLock.unlock();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        }

    }

}

class Persoana extends Thread{

    private int numarOrdine=0;//numarul de ordine al acestei persoana de la ghiseul curent
    private boolean am_intrat=false; //verifica daca aceasta persoana este deja in coada
    private boolean documentObtinut=false; //verifica daca aceasta persoana a obtinut documentul de la ghiseul curent
    private Ghiseu gs; //ghiseul la care asteapta aceasta persoana

    public void run(){

        while(esteDocumentulObtinut()==false) {

            gs.serveste(this);
        }
    }
    public void setNumarordine(int numarOdrine){
        this.numarOrdine=numarOdrine;
    }

    public int getNumarordine(){
        return numarOrdine;
    }

    public boolean esteDocumentulObtinut(){ return documentObtinut; }
    public void setDocumentObtinut(){
        documentObtinut=true;
    }

    public void intraInCoada(){
        am_intrat=true;
    }

    public boolean esteInCoada(){
        return am_intrat;
    }

    public void setGhiseu(Ghiseu gs){
        this.gs=gs;
    }
    public Ghiseu getGhiseu(){
        return gs;
    }

}


public class Simulare {

    public static void main(String []argv){
        int n = 40; // Number people
        Ghiseu ghis=new Ghiseu();
        ghis.start();
        for (int i=0; i<n; i++)
        {
            Persoana client = new Persoana();
            client.setGhiseu(ghis);
            client.start();
        }
    }
}
