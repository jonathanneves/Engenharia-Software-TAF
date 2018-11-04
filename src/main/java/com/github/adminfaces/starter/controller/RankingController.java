package com.github.adminfaces.starter.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.Session;

import com.github.adminfaces.starter.model.Ranking;
import com.github.adminfaces.starter.model.Taf;
import com.github.adminfaces.starter.model.TafAluno;
import com.github.adminfaces.starter.model.TafExercicio;
import com.github.adminfaces.starter.model.Usuario;
import com.github.adminfaces.starter.util.HibernateUtil;

@ManagedBean
@ViewScoped
public class RankingController implements Serializable {

private static final long serialVersionUID = 1L;

	private TafAluno tafaluno;
 
	private Taf tafselecionado;
	private TafExercicio exercicioSel;
	private String filtroSel;
	
	private List<TafAluno> tafalunos;
	private List<TafExercicio> tafexercicios;
	private List<Usuario> usuarios;
	
	private List<Taf> tafsrealizadas;			//tafs que foram realizadas 'S'
	private List<TafAluno> alunosparticipantes;	//alunos que participaram da taf selecionada;
	private List<TafAluno> alunosclassificados; //alunos com filtro forte fraco ou medio
	private List<TafAluno> alunosporexercicio; //alunos por exercicio
	
	//private List<Integer> somaTotais;
	//private String cabecalho;
	//private List<String> corpo;
	
	private boolean desativadoPont = true;			//desativar Botao antes de selecionar 3 combo
	private boolean desativadoExer = true;
	private int desativarBotaoPont = 0;
	private int desativarBotaoExer = 0;
	
	/*private List<ColumnModel> colunas;
	private List<String[]> rankingAluno;
	private String colunaNome;*/
	     
	@PostConstruct
	public void inicializa() {
		listarTodas();
		listarTafExercicios();
		listarTafsRealizadas();
		filtrarAlunosTaf();
		tafselecionado = tafsrealizadas.get(0);
		criarCabecalho();
		ranqueandoAluno();
		//addRanking();
	    //createDynamicColumns();
	}
	
	public void listarTodas() {
		Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
		try {
			CriteriaQuery<TafAluno> cq = sessao.getCriteriaBuilder().createQuery(TafAluno.class);
			cq.from(TafAluno.class);
			tafalunos = sessao.createQuery(cq).getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			addErro("ERRO", "Erro ao listar alunos  participantes");
		} finally {
			sessao.close();
		}
	}
	
	public List<TafExercicio> listarTafExercicios() {
		Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
		try {
			if(tafselecionado != null) {
				CriteriaQuery<TafExercicio> cq = sessao.getCriteriaBuilder().createQuery(TafExercicio.class);
				cq.from(TafExercicio.class);
				tafexercicios = sessao.createQuery(cq).getResultList();
				tafexercicios.removeIf(s -> s.getTaf().getId() != tafselecionado.getId());
				for(TafExercicio t : tafexercicios)
					System.out.println(t.getExercicio().getNome());
			}else
				System.out.println("TAF SELECIONADO É NULO");
		} catch (Exception e) {
			addErro("ERRO", "Erro ao listar tafs exercicios");
		} finally {
			sessao.close();
		}
		return tafexercicios;
	}
		
	public List<Taf> listarTafsRealizadas() {
		Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
		try {
			CriteriaQuery<Taf> cq = sessao.getCriteriaBuilder().createQuery(Taf.class);
			cq.from(Taf.class);
			tafsrealizadas = sessao.createQuery(cq).getResultList();
			tafsrealizadas.removeIf(s -> s.getRealizado().equals("N"));
		} catch (Exception e) {
			addErro("ERRO", "Erro ao filtrar tafs");
		} finally {
			sessao.close();
		}	
		return tafsrealizadas;
	}
	
	public List<TafAluno> filtrarAlunosTaf(){
		Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
		try {
			if(tafselecionado != null) {
				CriteriaQuery<TafAluno> cq = sessao.getCriteriaBuilder().createQuery(TafAluno.class);
				cq.from(TafAluno.class);
				alunosparticipantes = sessao.createQuery(cq).getResultList();
				alunosparticipantes.removeIf(s -> s.getTafexercicio().getTaf().getId() != tafselecionado.getId());
			} else
				System.out.println("Nenhuma taf selecionado");
		} catch (Exception e) {
			addErro("ERRO", "Erro ao filtrar tafs");
		} finally {
			sessao.close();
		}	
		return alunosparticipantes;
	}
	 
	public List<TafAluno> filtrarPorExercicio(){
		Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
		try {
			if(tafselecionado != null) {
				CriteriaQuery<TafAluno> cq = sessao.getCriteriaBuilder().createQuery(TafAluno.class);
				cq.from(TafAluno.class);
				alunosporexercicio = sessao.createQuery(cq).getResultList();
				alunosporexercicio.removeIf(s -> s.getTafexercicio().getTaf().getId() != tafselecionado.getId());
				alunosporexercicio.removeIf(s -> s.getTafexercicio().getId() != exercicioSel.getId());
				alunosporexercicio.sort(Comparator.comparing(TafAluno::getPontuacao).reversed());
			} else
				System.out.println("Nenhuma taf selecionado");
		} catch (Exception e) {
			addErro("ERRO", "Erro ao filtrar tafs");
		} finally {
			sessao.close();
		}	
		return alunosporexercicio;
	}
	 
	public List<TafAluno> filtrarAlunosClassificados(){
		Session sessao = HibernateUtil.getFabricaDeSessoes().openSession();
		try {
			CriteriaQuery<TafAluno> cq = sessao.getCriteriaBuilder().createQuery(TafAluno.class);
			cq.from(TafAluno.class);
			alunosclassificados = sessao.createQuery(cq).getResultList();
			alunosclassificados.removeIf(s -> s.getTafexercicio().getTaf().getId() != tafselecionado.getId());
			alunosclassificados.removeIf(s -> s.getTafexercicio().getId() != exercicioSel.getId());
			if(filtroSel.equals("Fraco")) {
				if(exercicioSel.getModalidade().equals("1RM"))
					alunosclassificados.removeIf(s -> s.getTafexercicio().getExercicio().getUmrmFraco() < s.getPontuacao());
				if(exercicioSel.getModalidade().equals("MAX"))
					alunosclassificados.removeIf(s -> s.getTafexercicio().getExercicio().getRmFraco() < s.getPontuacao());
				if(exercicioSel.getModalidade().equals("VT"))
					alunosclassificados.removeIf(s -> s.getTafexercicio().getExercicio().getVtFraco() < s.getPontuacao());
			}else if(filtroSel.equals("Forte")) {
				if(exercicioSel.getModalidade().equals("1RM")) 
					alunosclassificados.removeIf(s -> s.getTafexercicio().getExercicio().getUmrmForte() > s.getPontuacao());
				if(exercicioSel.getModalidade().equals("MAX"))
					alunosclassificados.removeIf(s -> s.getTafexercicio().getExercicio().getRmForte() > s.getPontuacao());
				if(exercicioSel.getModalidade().equals("VT"))
					alunosclassificados.removeIf(s -> s.getTafexercicio().getExercicio().getVtForte() > s.getPontuacao());
			}else if(filtroSel.equals("Médio")) {
				if(exercicioSel.getModalidade().equals("1RM")) {
					alunosclassificados.removeIf(s -> s.getTafexercicio().getExercicio().getUmrmFraco() >= s.getPontuacao());
					alunosclassificados.removeIf(s-> s.getTafexercicio().getExercicio().getUmrmForte() <= s.getPontuacao());
				}
				if(exercicioSel.getModalidade().equals("MAX")) {
					alunosclassificados.removeIf(s -> s.getTafexercicio().getExercicio().getRmFraco() >= s.getPontuacao());
					alunosclassificados.removeIf(s -> s.getTafexercicio().getExercicio().getRmForte() <= s.getPontuacao());
				}
				if(exercicioSel.getModalidade().equals("VT")) {
					alunosclassificados.removeIf(s -> s.getTafexercicio().getExercicio().getVtFraco() >= s.getPontuacao());
					alunosclassificados.removeIf(s -> s.getTafexercicio().getExercicio().getVtForte() <= s.getPontuacao());
				}
			}
			alunosclassificados.sort(Comparator.comparing(TafAluno::getPontuacao));
		} catch (Exception e) {
			addErro("ERRO", "Erro ao classficar alunos");
		} finally {
			sessao.close();
		}	
		return alunosclassificados;
	}
	 
	  public List<Ranking> listarTotalPontos() {
		  List<Ranking>listaTotal = new ArrayList<Ranking>();
		  String texto="";
		  int saida=0;
		  boolean novoAluno = true;
		  int total = 0; 
		  alunosparticipantes = filtrarAlunosTaf();
		  tafexercicios = listarTafExercicios();
		  Collections.sort(alunosparticipantes, (o1, o2) -> o1.getUsuario().getId().compareTo(o2.getUsuario().getId()));
		  
		  for(TafAluno te : alunosparticipantes) {
			  if(novoAluno) {
				  texto = te.getUsuario().getNome();
			  	  novoAluno = false;
			  }
			  total += te.getPontuacao();
			  saida++;
			  if(tafexercicios.size() == saida) {
				  Ranking rank = new Ranking(texto, total);
				  listaTotal.add(rank);
				  novoAluno = true;
				  total=0; saida=0;
			  }
		  }
		  listaTotal.sort(Comparator.comparing(Ranking::getTotalpontos).reversed());
		  int pos = 0;
		  for(Ranking m : listaTotal) {
			  pos++;
			  m.setPosicao(pos+"º"); 
		  }
		  return listaTotal;
	  }
	  
	  
	  public List<Ranking> ranqueandoAluno() {
		  List<Ranking>corpo = new ArrayList<Ranking>();
		  String texto="";
		  int saida=0;
		  boolean novoAluno = true;
		  int total = 0; 
		  alunosparticipantes = filtrarAlunosTaf();
		  tafexercicios = listarTafExercicios();
		  Collections.sort(alunosparticipantes, (o1, o2) -> o1.getUsuario().getId().compareTo(o2.getUsuario().getId()));
		  
		  for(TafAluno te : alunosparticipantes) {
			  if(novoAluno) {
				  texto = te.getUsuario().getNome()+" |   ";
			  	  novoAluno = false;
			  }
			  texto += "   "+te.getPontuacao()+"   |   ";
			  total += te.getPontuacao();
			  saida++;
			  if(tafexercicios.size() == saida) {
				  Ranking rank = new Ranking(texto, total);
				  corpo.add(rank);
				  novoAluno = true;
				  total=0; saida=0;
			  }
		  }
		  corpo.sort(Comparator.comparing(Ranking::getTotalpontos).reversed());
		  int pos = 0;
		  for(Ranking m : corpo) {
			  pos++;
			  m.setTexto(" "+pos+"º "+m.getTexto()); 
		  }
		  return corpo;
	  }
	  
	   public String criarCabecalho() {
		   
		   String cab = "Pos |  Aluno  |";	       

		   tafexercicios = listarTafExercicios();
		   for(TafExercicio te : tafexercicios) {
		    		cab +=  " "+te.getExercicio().getNome()+"-"+te.getModalidade()+" | ";
		    }
		   cab += " Total";
		   System.out.println(cab);
		   return cab;
	   } 
	   

/*    private void addRanking() {
    	   
    	rankingAluno = new ArrayList<String[]>();
    	if(tafselecionado != null) {
			Collections.sort(alunosparticipantes, (o1, o2) -> o1.getUsuario().getId().compareTo(o2.getUsuario().getId()));
			
			for(TafAluno ta : alunosparticipantes) {
				System.out.println(ta.getUsuario().getNome() +" = "+ ta.getPontuacao());
			}
	  
		    Taf tafprimeira = null;
		    boolean primeiraVez = true;
		    int index=0;
		    Usuario alunoNome = null;
		    String[] vetor = new String[tafexercicios.size()];
	
		    for(TafExercicio ta : tafexercicios) {
			   tafprimeira	= ta.getTaf();
			   break;
		    }
		    
		    for(TafAluno ta : alunosparticipantes) {
		    	System.out.println("TESTANDO");
		    	if(primeiraVez) {
		    	    vetor =  new String[tafexercicios.size()];
		    		alunoNome = ta.getUsuario();
		    		System.out.println("NOVO ALUNO "+ta.getUsuario().getNome());
		    		tafprimeira = ta.getTafexercicio().getTaf();
		    		vetor[index] = alunoNome.getNome();
		    		index++;
		    		primeiraVez = false;
		    	}
		    	System.out.println("ATUAL"+alunoNome.getNome());
		    	if(ta.getTafexercicio().getTaf() == tafprimeira) {
		        	System.out.println("Alo"+ta.getPontuacao());
		        	vetor[index] = Integer.toString(ta.getPontuacao());	     
		        	index++;
		    		if(alunoNome != ta.getUsuario()) {
		    			System.out.println("teste");
		      			index = 0;
		      			primeiraVez = true;
		      			rankingAluno.add(vetor); 			
		    		}
		    	}else
		      		break;    		
		    }
		    for(String[] x : rankingAluno) {
		    	for(int i=0; i<x.length; i++)
		    		System.out.println("NOME: "+x[i]);
		    	System.out.println("PROXIMO");
		    }
    	}else
    		System.out.println("Selecione TAF");
    }
	
   private void createDynamicColumns() {
 
	   int index = 0;
       colunas = new ArrayList<ColumnModel>();
       //System.out.println(">>>>"+rankingAluno.get(0)[0]+"--"+rankingAluno.get(0)[1]);
       colunas.add(new ColumnModel("Aluno", rankingAluno.get(0)[index]));		  
       index++; 
       

	   for(TafExercicio te : tafexercicios) {
	    	if(te.getTaf() == tafselecionado) {
	    		colunas.add(new ColumnModel(te.getExercicio().getNome()+"-"+te.getModalidade(),rankingAluno.get(0)));
	    		index++;
	    	} else
	    		break;
	    }
	   
	   colunas.add(new ColumnModel("Total",rankingAluno.get(0)));
    } */
	    
	public void manterTaf() {
		System.out.println("Nome: "+getTafselecionado().getNome() +"  Data: "+ getTafselecionado().getData());
		listarTafExercicios();
	}

	public void manterExercicio() {
		System.out.println("Nome: "+getExercicioSel().getExercicio().getNome() +"  Modalidade: "+ getExercicioSel().getModalidade());
		desativarBotaoExer++;
		desativarBotaoPont++;
		if(desativarBotaoPont == 2)
			setDesativadoPont(false);
		if(desativarBotaoExer == 1)
			setDesativadoExer(false);
	}
	
	public void manterFiltro() {
		System.out.println("FILTRO: "+filtroSel);
		desativarBotaoPont++;
		if(desativarBotaoPont == 2)
			setDesativadoPont(false);
	}
	public Taf getTafselecionado() {
		return tafselecionado;
	}

	public void setTafselecionado(Taf tafselecionado) {
		this.tafselecionado = tafselecionado;
	}

	public void selecionarExercicio(ActionEvent evt){
		exercicioSel = (TafExercicio)evt.getComponent().getAttributes().get("exercicioSeleciona");
		System.out.println("Exercicio: "+ exercicioSel.getExercicio().getNome());
	}
		
	public List<TafExercicio> getTafexercicios() {
		return tafexercicios;
	}

	public void setTafexercicios(List<TafExercicio> tafexercicios) {
		this.tafexercicios = tafexercicios;
	}

	public List<Usuario> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(List<Usuario> usuarios) {
		this.usuarios = usuarios;
	}

	public TafAluno getTafaluno() {
		return tafaluno;
	}

	public void setTafaluno(TafAluno tafaluno) {
		this.tafaluno = tafaluno;
	}

	public List<TafAluno> getTafalunos() {
		return tafalunos;
	}

	public void setTafalunos(List<TafAluno> tafalunos) {
		this.tafalunos = tafalunos;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	public TafExercicio getExercicioSel() {
		return exercicioSel;
	}

	public void setExercicioSel(TafExercicio exercicioSel) {
		this.exercicioSel = exercicioSel;
	}

	public List<Taf> getTafsrealizadas() {
		return tafsrealizadas;
	}

	public void setTafsrealizadas(List<Taf> tafsrealizadas) {
		this.tafsrealizadas = tafsrealizadas;
	}

	public List<TafAluno> getAlunosparticipantes() {
		return alunosparticipantes;
	}

	public void setAlunosparticipantes(List<TafAluno> alunosparticipantes) {
		this.alunosparticipantes = alunosparticipantes;
	}

	public List<TafAluno> getAlunosclassificados() {
		return alunosclassificados;
	}

	public void setAlunosclassificados(List<TafAluno> alunosclassificados) {
		this.alunosclassificados = alunosclassificados;
	}

	public int getDesativarBotaoPont() {
		return desativarBotaoPont;
	}

	public void setDesativarBotaoPont(int desativarBotaoPont) {
		this.desativarBotaoPont = desativarBotaoPont;
	}

	public int getDesativarBotaoExer() {
		return desativarBotaoExer;
	}

	public void setDesativarBotaoExer(int desativarBotaoExer) {
		this.desativarBotaoExer = desativarBotaoExer;
	}

	public void addErro(String summary, String detail) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, detail);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
	
	public void addMessage(String summary, String detail) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public String getFiltroSel() {
		return filtroSel;
	}

	public void setFiltroSel(String filtroSel) {
		this.filtroSel = filtroSel;
	}

	public List<TafAluno> getAlunosporexercicio() {
		return alunosporexercicio;
	}

	public void setAlunosporexercicio(List<TafAluno> alunosporexercicio) {
		this.alunosporexercicio = alunosporexercicio;
	}

	public boolean isDesativadoPont() {
		return desativadoPont;
	}

	public void setDesativadoPont(boolean desativadoPont) {
		this.desativadoPont = desativadoPont;
	}

	public boolean isDesativadoExer() {
		return desativadoExer;
	}

	public void setDesativadoExer(boolean desativadoExer) {
		this.desativadoExer = desativadoExer;
	}


	static public class ColumnModel implements Serializable {
    	 
        private String header;
        private String[] property;
 
        public ColumnModel(String header, String[] property) {
            this.header = header;
            this.property = property;
        }
 
        public String getHeader() {
            return header;
        }

		public String[] getProperty() {
			return property;
		}     
    }
}