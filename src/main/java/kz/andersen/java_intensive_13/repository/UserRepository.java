package kz.andersen.java_intensive_13.repository;

import kz.andersen.java_intensive_13.config.HibernateConfig;
import kz.andersen.java_intensive_13.models.User;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Slf4j
public class UserRepository {

    private SessionFactory sessionFactory = HibernateConfig.buildSessionFactory();

    public Optional<User> findUserById(Long userId) {
        if (userId == null) {
            log.warn("User ID is null, returning empty result.");
            return Optional.empty();
        }

        try (Session session = sessionFactory.openSession()) {
            User user = session.get(User.class, userId);

            if (user != null) {
                log.info("Found Apartment: {}", user);
                return Optional.of(user);
            } else {
                log.info("No Apartment found with ID: {}", userId);
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("Error occurred while retrieving user with ID: {}", userId, e);
            throw new RuntimeException("Error retrieving user", e);
        }
    }

    public void saveUser(User user) {
        if (user == null) {
            log.warn("Apartment is null, nothing to save.");
            return;
        }

        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
            log.info("User saved: {}", user);
        } catch (Exception e) {
            if (transaction != null && transaction.getStatus().canRollback()) {
                transaction.rollback();
            }
            log.error("Error occurred while saving user: {}", user, e);
            throw new RuntimeException("Failed to save user", e);
        }
    }

    public List<User> findAllUsers() {
        List<User> users = new ArrayList<>();
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            Query<User> query = session.createQuery(
                    "from User",User.class);

            users = query.getResultList();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.getStatus().canRollback()) {
                transaction.rollback();
            }
            log.error("Error occurred while retrieving users", e);
        }
        return users;
    }
}
