package Application.classes;

import org.hibernate.*;
import org.hibernate.cfg.Configuration;


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

    //public static <T> ObservableList<T> executeQuery() {return null;}

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void close() {
        getSessionFactory().close();
    }
}