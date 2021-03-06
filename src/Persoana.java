import java.util.ArrayList;
import java.util.List;

import sun.misc.Lock;

public class Persoana extends Thread{

    private int numarOrdine=0;//numarul de ordine al acestei persoana de la ghiseul curent
    private boolean am_intrat=false; //verifica daca aceasta persoana este deja in coada
    public boolean documentObtinut=false; //verifica daca aceasta persoana a obtinut documentul de la ghiseul curent
    private Ghiseu gs; //ghiseul la care asteapta aceasta persoana
    private List<String> requiredDocs;
    private List<String> ownedDocs = new ArrayList<String>();
    private List<Birou> birouList;
    public int indiceNumar;

    public Persoana(List<String> acte , List<Birou> birouri,int indiceNumar) {
    	requiredDocs=acte;
    	birouList=birouri;
    	this.indiceNumar=indiceNumar;
    }
    
    public void run(){

    	
    	while(true) {

    		String act= requiredDocs.get(0);
    		Birou birou = getBirouForAct(act);
    		if(birou==null)
    			break;
    		if(!canObtainAct(birou, act))
    			continue;

    		gs = getGhiseuFromBirou(birou);
        gs.serveste(this);
        if(!requiredDocs.isEmpty())
        requiredDocs.remove(0);
        ownedDocs.add(act);

        if(requiredDocs.isEmpty())
        	break;
    	
    	}
    	System.out.println("Persoana cu numarul de ordine "+indiceNumar+" a obtinut documentele");


    }
    
    private Ghiseu getGhiseuFromBirou(Birou birou) {
        //cand verificati daca caserita este in pauza apelati gs.getLockPauza() care va intoarce un lock si apoi incercati sa obtineti
        // locul respectiv iar pe urma cititi flagPauza care spune daca caserita e in pauza sau nu
    	
    	List<Ghiseu> ghs = birou.getGhiseuri();
    	Ghiseu g = ghs.get(0);
    	boolean found = false;
    	for (int i=0;i< ghs.size() && !found;i++)
    	{
    		Ghiseu gs = ghs.get(i);
    		Lock lock = gs.getLockPauza();
    		try {
    			lock.lock();
    			if (!gs.flagPauza)
    			{
    				g = gs;
    				found = true;
    			}
    			lock.unlock();
    		}
    		catch (InterruptedException e)
    		{
    			e.printStackTrace();
    		}
    	}
    	
    	return g;
    	//return null;
    }
    
    private Birou getBirouForAct(String act){ 
    	Birou birou=birouList.stream().filter(b->b.getActe().contains(act)).findFirst().orElse(null);
    	return birou;
    }
    
    private boolean canObtainAct(Birou birou,String act) {
    	List<String> requiredActs=birou.getDependenteActe().get(act);
    	List<String> notOwned = new ArrayList<String>();
    	for(String acct:requiredActs) {
    		if(!ownedDocs.contains(acct))
    			notOwned.add(acct);
    	}
    	if(notOwned.isEmpty())
    		return true;
    	for(String acct:notOwned) {
    		if(requiredDocs.contains(acct))
    			requiredDocs.remove(acct);
    	}
    	requiredDocs.addAll(0, notOwned);
    	return false;
    }

    public void setNumarordine(int numarOdrine){
        this.numarOrdine=numarOdrine;
    }

    public int getNumarordine(){
        return numarOrdine;
    }

    public boolean esteDocumentulObtinut(){ 
    	return documentObtinut; 
    }
    
    public void setDocumentObtinut(){
        documentObtinut=true;
    }

    public void intraInCoada(){
        am_intrat=true;
    }

    public boolean esteInCoada(){
        return am_intrat;
    }

    private void setGhiseu(Ghiseu gs){
        this.gs=gs;
    }
    public Ghiseu getGhiseu(){
        return gs;
    }

}