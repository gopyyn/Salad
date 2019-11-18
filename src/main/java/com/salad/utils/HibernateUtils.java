package com.salad.utils;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.Map;

import static com.salad.core.SaladContext.configProperties;
import static java.util.Optional.ofNullable;

public class HibernateUtils {
    private static StandardServiceRegistry registry;
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Map<String, String> hibernateConfig = configProperties.get("hibernate");
                ofNullable(hibernateConfig)
                        .orElseThrow(() -> new RuntimeException("config yaml missing database configuration"));

                StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder();

                registryBuilder.applySettings(hibernateConfig);

                registry = registryBuilder.build();

                MetadataSources sources = new MetadataSources(registry);

                Metadata metadata = sources.getMetadataBuilder().build();

                sessionFactory = metadata.buildSessionFactory();

            } catch (Exception e) {
                e.printStackTrace();
                if (registry != null) {
                    StandardServiceRegistryBuilder.destroy(registry);
                }
            }
        }
        return sessionFactory;
    }

    public static Session getSession() {
        try{
            return getSessionFactory().getCurrentSession();
        } catch (HibernateException e) {
            return getSessionFactory().openSession();
        }
    }

    public static void closeSession() {
        if (sessionFactory != null && sessionFactory.isOpen()) {
            sessionFactory.close();
        }
    }

    public static void shutdown() {
        if (registry != null) {
            StandardServiceRegistryBuilder.destroy(registry);
        }
        sessionFactory = null;
    }
}
