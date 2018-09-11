package com.github.adminfaces.starter.util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import com.github.adminfaces.starter.model.Usuario;

public class HibernateUtil {
	private static SessionFactory fabricaDeSessoes = criarFabricaDeSessoes();

	public static SessionFactory getFabricaDeSessoes() {
		return fabricaDeSessoes; 
	}

	private static SessionFactory criarFabricaDeSessoes() {
		try {
			
			Configuration configuracao = new Configuration().configure();
			configuracao.addAnnotatedClass(Usuario.class);
			ServiceRegistry registro = new StandardServiceRegistryBuilder().applySettings(configuracao.getProperties()).build();
			
			SessionFactory fabrica = configuracao.buildSessionFactory(registro);
			
			return fabrica;
		} catch (Throwable ex) {
			System.err.println("A fábrica de "
					+ "sessões não pode ser criada." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}
}
