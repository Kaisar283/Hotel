package kz.andersen.java_intensive_13.config;

import kz.andersen.java_intensive_13.hibernate.convertor.ZonedDateTimeConvertor;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateConfig {

    public static SessionFactory buildSessionFactory(){
        Configuration configuration = new Configuration();
        configuration.configure();
        configuration.addAttributeConverter(new ZonedDateTimeConvertor());
        return configuration.buildSessionFactory();
    }
}
