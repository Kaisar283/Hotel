package kz.andersen.java_intensive_13.repository;

import kz.andersen.java_intensive_13.enums.UserRole;
import kz.andersen.java_intensive_13.models.User;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {

    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private Session session;

    @Mock
    private Transaction transaction;

    @InjectMocks
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        when(sessionFactory.openSession()).thenReturn(session);
    }

    @Test
    void findUserById_userExists_returnsUser() {
        Long userId = 1L;
        User mockUser = new User();
        mockUser.setId(userId);

        when(session.get(User.class, userId)).thenReturn(mockUser);

        Optional<User> result = userRepository.findUserById(userId);

        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getId());
        verify(sessionFactory).openSession();
        verify(session).close();
    }

    @Test
    void findUserById_userDoesNotExist_returnsEmpty() {
        Long userId = 1L;

        when(session.get(User.class, userId)).thenReturn(null);

        Optional<User> result = userRepository.findUserById(userId);

        assertFalse(result.isPresent());
        verify(sessionFactory).openSession();
        verify(session).close();
    }

    @Test
    void saveUser_validUser_savesUser() {
        User user = new User();
        user.setUserRole(UserRole.USER);
        user.setFirstName("John");

        when(session.beginTransaction()).thenReturn(transaction);

        userRepository.saveUser(user);

        verify(sessionFactory).openSession();
        verify(session).persist(user);
        verify(transaction).commit();
        verify(session).close();
    }

    @Test
    void saveUser_transactionFails_rollsBack() {
        User user = new User();

        when(session.beginTransaction()).thenReturn(transaction);
        doThrow(new RuntimeException("Test exception")).when(session).persist(user);
        when(transaction.getStatus()).thenReturn(TransactionStatus.ACTIVE);

        assertThrows(RuntimeException.class, () -> userRepository.saveUser(user));

        verify(transaction).rollback();
        verify(session).close();
    }

    @Test
    void findAllUsers_returnsUserList() {
        List<User> mockUsers = List.of(new User(), new User());
        when(session.createQuery("from User", User.class)).thenReturn(mock(Query.class));
        Query<User> mockQuery = mock(Query.class);
        when(session.createQuery("from User", User.class)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(mockUsers);

        List<User> users = userRepository.findAllUsers();

        assertEquals(mockUsers.size(), users.size());
        verify(sessionFactory).openSession();
        verify(session).close();
    }

    @Test
    void findAllUsers_queryFails_rollsBack() {
        when(session.beginTransaction()).thenReturn(transaction);
        when(session.createQuery("from User", User.class)).thenThrow(new RuntimeException("Test exception"));
        when(transaction.getStatus()).thenReturn(TransactionStatus.ACTIVE);

        List<User> users = userRepository.findAllUsers();

        assertTrue(users.isEmpty());
        verify(transaction).rollback();
        verify(session).close();
    }
}
