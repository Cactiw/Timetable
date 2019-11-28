package classes;

import org.hibernate.*;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.spi.FilterDefinition;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.stat.Statistics;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.SynchronizationType;
import javax.persistence.criteria.CriteriaBuilder;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Set;



public class HibernateUtil implements AutoCloseable {
    private static final SessionFactory sessionFactory;
    static {
        try {
            sessionFactory =  new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed. " + ex);
            throw new ExceptionInInitializerError(ex);
        }

    }

//    private static void initiateICU() {
//        Session session = HibernateUtil.getSessionFactory().openSession();
//        Transaction tx = null;
//        try {
//            tx = session.beginTransaction();
//            SQLQuery query = session.createSQLQuery("sqlite3_db_config()");
//            List<Object[]> rows = query.list();
//        } catch (Throwable ex) {
//            System.err.println("Initial SessionFactory creation failed. " + ex);
//            throw new ExceptionInInitializerError(ex);
//        }
//    }

    public static <T> int createObject(T object) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try  {
            tx = session.beginTransaction();
            session.save(object);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return -1;
        }
        finally {
            session.close();
        }
        return 0;
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void close() {
        getSessionFactory().close();
    }
}