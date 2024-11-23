package kz.andersen.java_intensive_13.repository;

import kz.andersen.java_intensive_13.config.HibernateConfig;
import kz.andersen.java_intensive_13.models.Apartment;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.*;

import static kz.andersen.java_intensive_13.statics.ApartmentHQL.*;

@Slf4j
public class ApartmentStorage {

    private SessionFactory sessionFactory = HibernateConfig.buildSessionFactory();

    /**
     * Method: add apartment to original list.
     *
     * @param apartment - an Apartment instance
     */
    public void addApartment(Apartment apartment) {
        if (apartment == null) {
            log.warn("Apartment is null, nothing to save.");
            return;
        }

        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(apartment);
            transaction.commit();
            log.info("Apartment saved: {}", apartment);
        } catch (Exception e) {
            if (transaction != null && transaction.getStatus().canRollback()) {
                transaction.rollback();
            }
            log.error("Error occurred while saving apartment: {}", apartment, e);
            throw new RuntimeException("Failed to save apartment", e);
        }
    }


    public void updateApartment(Apartment apartment) {
        if (apartment == null) {
            log.warn("Apartment is null, nothing to update.");
            return;
        }

        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            Apartment mergedApartment = session.merge(apartment);
            transaction.commit();
            log.info("Apartment updated: {}", mergedApartment);
        } catch (Exception e) {
            if (transaction != null && transaction.getStatus().canRollback()) {
                transaction.rollback();
            }
            log.error("Error occurred while updating apartment: {}", apartment, e);
            throw new RuntimeException("Failed to update apartment", e);
        }
    }


    /**
     * Return Optional<Apartment> if given ID it exists in the Apartment Storage,
     * else return an empty Optional
     *
     * @param apartmentId apartment ID
     * @return an Apartment Optional by given ID
     */
    public Optional<Apartment> getApartmentById(Integer apartmentId) {
        if (apartmentId == null) {
            log.warn("Apartment ID is null, returning empty result.");
            return Optional.empty();
        }

        try (Session session = sessionFactory.openSession()) {
            Apartment apartment = session.get(Apartment.class, apartmentId);

            if (apartment != null) {
                log.info("Found Apartment: {}", apartment);
                return Optional.of(apartment);
            } else {
                log.info("No Apartment found with ID: {}", apartmentId);
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("Error occurred while retrieving apartment with ID: {}", apartmentId, e);
            throw new RuntimeException("Error retrieving apartment", e);
        }
    }


    /**
     * @return the original Apartments list
     */
    public List<Apartment> getApartments() {
        return getApartmentListByGivenHQL(FIND_ALL_APARTMENT);
    }

    /**
     * @return a new ArrayList sorted by Apartment price, in ASC order.
     */
    public List<Apartment> sortApartmentByPrice() {
        return getApartmentListByGivenHQL(SORT_BY_PRICE_HQL);
    }


    /**
     * @return a new ArrayList sorted by Apartment ID, in ASC order.
     */
    public List<Apartment> sortApartmentById() {
        return getApartmentListByGivenHQL(SORTED_BY_ID_HQL);
    }

    /**
     * @return a new ArrayList sorted by User name, in DESC order.
     */
    public List<Apartment> sortApartmentByClientName() {
        return getApartmentListByGivenHQL(SORT_BY_USERNAME_HQL);
    }


    /**
     * @return a new ArrayList sorted by Reservation status, in DESC order.
     */
    public List<Apartment> sortedApartmentByReservationStatus() {
        return getApartmentListByGivenHQL(SORT_BY_RESERVATION_STATUS_HQL);
    }

    private void checkSessionFactory(){
        if (sessionFactory == null) {
            log.error("SessionFactory is not initialized.");
            throw new IllegalStateException("SessionFactory is not initialized.");
        }
    }

    private List<Apartment> getApartmentListByGivenHQL(String hql){
        checkSessionFactory();

        List<Apartment> apartments = new ArrayList<>();
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            Query<Apartment> query = session.createQuery(
                    hql,Apartment.class);

            apartments = query.getResultList();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.getStatus().canRollback()) {
                transaction.rollback();
            }
            log.error("Error occurred while retrieving apartments", e);
        }
        return apartments;
    }
}
