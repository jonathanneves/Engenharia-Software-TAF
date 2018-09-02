package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import model.Stats;
 
public class TafRanking implements Serializable {
 
	private static final long serialVersionUID = 1L; 
	
    private String name;    
    private List<Stats> stats;
     
    public TafRanking() {
        stats = new ArrayList<Stats>();
    }
     
    public TafRanking(String name) {
        this.name = name;
        stats = new ArrayList<Stats>();
    }
 
    public String getName() {
        return name;
    }
 
    public void setName(String name) {
        this.name = name;
    }
 
    public List<Stats> getStats() {
        return stats;
    }
 
    public void setStats(List<Stats> stats) {
        this.stats = stats;
    }
     

}