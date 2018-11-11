import sun.misc.Lock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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
                System.out.println("Clientul " +
                        ps.indiceNumar +
                        " a intrat in coada");
                lock.unlock();

                synchronized (ps) {
                    ps.wait();
                }

                System.out.println("Clientul " +
                        ps.indiceNumar +
                        " is running");

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
							((Persoana)client).documentObtinut=false;
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
    	List<Ghiseu> g1 = new ArrayList<Ghiseu>();
    	g1.add(new Ghiseu());
    	g1.add(new Ghiseu());
    	g1.add(new Ghiseu());
    	
    	List<String> l1 = new ArrayList<String>();
    	l1.add("Buletin");
    	l1.add("Act proprietate");
    	
    	HashMap<String,List<String>> m1 = new HashMap<String,List<String>>();
    	
    	Birou b1 = new Birou(g1,l1,m1);
    	b1.addDependentaAct("Buletin", Arrays.asList());
    	b1.addDependentaAct("Act proprietate", Arrays.asList());
    	
    	List<Ghiseu> g2 = new ArrayList<Ghiseu>();
    	g2.add(new Ghiseu());
    	g2.add(new Ghiseu());
    	
    	List<String> l2 = new ArrayList<String>();
    	l2.add("Carte funciara");
    	l2.add("Certificat calitate");
    	
    	HashMap<String,List<String>> m2 = new HashMap<String,List<String>>();
    	
    	Birou b2 = new Birou(g2,l2,m2);
    	List<String> d21 = new ArrayList<String>();
    	d21.add("Buletin");
    	d21.add("Act proprietate");
    	b2.addDependentaAct("Carte funciara", d21);
    	List<String> d22 = new ArrayList<String>();
    	d22.add("Act proprietate");
    	b2.addDependentaAct("Certificat calitate", d22);
    	
    	List<Ghiseu> g3 = new ArrayList<Ghiseu>();
    	g3.add(new Ghiseu());
    	
    	List<String> l3 = new ArrayList<String>();
    	l3.add("Contract vanzare cumparare");
    	
    	HashMap<String,List<String>> m3 = new HashMap<String,List<String>>();
    	
    	Birou b3 = new Birou(g3,l3,m3);
    	List<String> d31 = new ArrayList<String>();
    	d31.add("Buletin");
    	d31.add("Act proprietate");
    	d31.add("Certificat calitate");
    	b3.addDependentaAct("Contract vanzare cumparare", d31);
    	
    	List<String> a1 = new ArrayList<String>();
    	a1.add("Buletin");
    	a1.add("Certificat calitate");
    	
    	List<String> a2 = new ArrayList<String>();
    	a2.add("Act proprietate");
    	a2.add("Carte funciara");
    	
    	List<String> a3 = new ArrayList<String>();
    	a3.add("Buletin");
    	a3.add("Act proprietate");
    	a3.add("Carte funciara");
    	a3.add("Certificat calitate");
    	
    	List<String> a4 = new ArrayList<String>();
    	a4.add("Carte funciara");
    	a4.add("Certificat calitate");
    	a4.add("Contract vanzare cumparare");
    	
    	List<Birou> lb = new ArrayList<Birou>();
    	lb.add(b1);
    	lb.add(b2);
    	lb.add(b3);
    	
    	b1.startLucru();
    	b2.startLucru();
    	b3.startLucru();
    	
    	Persoana client = null;
    	
    	for (int i=0;i<40;i++)
    	{
    		switch (i%4)
    		{
	    		case 0:
	    		{
	    			client = new Persoana(a1,lb,i);
	    			break;
	    		}
	    		case 1:
	    		{
	    			client = new Persoana(a2,lb,i);
	    			break;
	    		}
	    		case 2:
	    		{
	    			client = new Persoana(a3,lb,i);
	    			break;
	    		}
	    		case 3:
	    		{
	    			client = new Persoana(a4,lb,i);
	    			break;
	    		}
	    		default:break;
    		}
    		client.start();
    	}
    }
}
