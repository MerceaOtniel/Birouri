import sun.misc.Lock;

class Ghiseu{

    private int clientiTotali=0;
    private int clientCurent=0;
    private Lock lock = new Lock();

    public void serveste(Persoana ps){

        try {
            lock.lock();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(ps.getNumarordine()==clientCurent) {

            try {

                // Displaying the thread that is running
                System.out.println("Thread " +
                        Thread.currentThread().getId() +
                        " is running" + "    clientii        " + clientiTotali + "   cu numarul de ordine   "+ps.getNumarordine());

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
        for (int i=0; i<n; i++)
        {
            Persoana client = new Persoana();
            client.setGhiseu(ghis);
            client.start();
        }
    }
}
