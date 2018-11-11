import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Birou{

  private List<Ghiseu> ghiseuri;
  private List<String> acte;
  private HashMap<String, List<String>> dependenteActe;
  private int id;
  private static int ids = 0;

  public Birou() {
    id = ids;
    ids++;
  }

  public Birou(List<Ghiseu> g, List<String> a, HashMap<String, List<String>> d) {
    id = ids;
    ghiseuri = g;
    acte = a;
    dependenteActe = d;
    ids++;
  }

  public boolean addGhiseu(Ghiseu g) {
    return this.ghiseuri.add(g);
  }

  public boolean addAct(String a) {
    return this.acte.add(a);
  }

  public void addDependentaAct(String a, List<String> d) {
    if(this.dependenteActe.containsKey(a)) {
      List<String> tmp = this.dependenteActe.get(a);
      tmp.addAll(d);
      this.dependenteActe.put(a, tmp);
    } else {
      this.dependenteActe.put(a, d);
    }
  }

  public int getId() {
    return id;
  }

  public void setId(int idx) {
    id = idx;
  }

  public List<Ghiseu> getGhiseuri() {
    return ghiseuri;
  }

  public List<String> getActe() {
    return acte;
  }

  public Map<String, List<String>> getDependenteActe() {
    return dependenteActe;
  }

  public String toString() {
    return "Birou " + id;
  }

  public void startLucru() {
	  for (Ghiseu g:ghiseuri)
		  g.start();
  }
}
