package model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import model.TafRanking;
import model.Stats;

 
@ManagedBean(name="dtSubTableView")
public class SubTableView {
     
    private List<TafRanking> tafs;
    
 
    @PostConstruct
    public void init() {
        tafs = new ArrayList<TafRanking>();
        TafRanking Ranking = new TafRanking("TAF 1 - ...");
        Ranking.getStats().add(new Stats("1", "Custela", "105", "45", "30", 180));
        Ranking.getStats().add(new Stats("2", "Adriano", "100", "50", "15", 165));
        Ranking.getStats().add(new Stats("3", "Jonathan", "80", "60", "20", 160));
        tafs.add(Ranking);
         
      
    }
     
    public List<TafRanking> getTeams() {
        return tafs;
    }
}