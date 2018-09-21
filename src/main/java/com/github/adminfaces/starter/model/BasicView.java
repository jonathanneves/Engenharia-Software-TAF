package com.github.adminfaces.starter.model;
 
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
 
@ManagedBean(name="treeBasicView")
@ViewScoped
public class BasicView implements Serializable {
     
	private static final long serialVersionUID = 1L;
	private TreeNode root;
	private TreeNode root2;
	
    @PostConstruct
    public void init() {
        root = new DefaultTreeNode("TAF 1 - 03/10/2018", null);
        TreeNode taf1 = new DefaultTreeNode("TAF 1 - 03/10/2018", root);
       
        TreeNode taf1ex1 = new DefaultTreeNode("Exercicio 1", taf1);
        TreeNode taf1ex2 = new DefaultTreeNode("Exercicio 2", taf1);
        TreeNode taf1ex3 = new DefaultTreeNode("Exercicio 3", taf1);
        TreeNode taf1ex4 = new DefaultTreeNode("Exercicio 4", taf1);
        TreeNode taf1ex5 = new DefaultTreeNode("Exercicio 5", taf1);
        TreeNode taf1ex6 = new DefaultTreeNode("Exercicio 6", taf1);
        
        root2 = new DefaultTreeNode("TAF 2 - 23/10/2018", null);
        TreeNode taf2 = new DefaultTreeNode("TAF 2 - 23/10/2018", root2);
       
        TreeNode taf2ex1 = new DefaultTreeNode("Exercicio 1", taf2);
        TreeNode taf2ex2 = new DefaultTreeNode("Exercicio 2", taf2);
        TreeNode taf2ex3 = new DefaultTreeNode("Exercicio 3", taf2);
        TreeNode taf2ex4 = new DefaultTreeNode("Exercicio 4", taf2);
        TreeNode taf2ex5 = new DefaultTreeNode("Exercicio 5", taf2);
        TreeNode taf2ex6 = new DefaultTreeNode("Exercicio 6", taf2);
                   
    }
 
    public TreeNode getRoot2() {
		return root2;
	}

	public TreeNode getRoot() {
        return root;
    }
}