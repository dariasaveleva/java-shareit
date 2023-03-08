package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase
@RunWith(SpringRunner.class)
class BookingRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private BookingRepository repository;

    User user = new User(null, "name", "email@email.ru");
    User user2 = new User(null, "name1", "email1@email.ru");
    Item item = new Item(null, "name1", "description1", true,
            user, null);
    Booking booking = new Booking(null, LocalDateTime.now().minusHours(3),
            LocalDateTime.now().minusHours(1), item, user2, null);

    public PageRequest getPage() {
        return  PageRequest.of(0,10);
    }

    public void persistBooking() {
        testEntityManager.persist(booking);
    }

    @BeforeEach
    public void persistTestData() {
        testEntityManager.persist(user);
        testEntityManager.persist(user2);
        testEntityManager.persist(item);
    }

    @Test
    public void contextLoads() {
        assertNotNull(testEntityManager);
    }

    @Test
    public void findAllByBookerIdOrderByStartDescTest() {
       persistBooking();

        List<Booking> bookings = repository.findAllByBookerIdOrderByStartDesc(user2.getId(), getPage());
        assertEquals(1, bookings.size());
        assertEquals(booking, bookings.get(0));
    }

    @Test
    public void findByBookerCurrentTest() {
        persistBooking();
        List<Booking> bookings = repository.findByBookerCurrent(user2.getId(),
                LocalDateTime.now().minusHours(2), getPage());
        assertEquals(1, bookings.size());
        assertEquals(booking, bookings.get(0));
    }

    @Test
    public void findByBookerPastTest() {
        persistBooking();
        List<Booking> bookings = repository.findByBookerPast(user2.getId(),
                LocalDateTime.now().plusHours(2), getPage());
        assertEquals(1, bookings.size());
        assertEquals(booking, bookings.get(0));
    }

    @Test
    public void findByBookerFutureTest() {
        persistBooking();
        List<Booking> bookings = repository.findByBookerFuture(user2.getId(),
                LocalDateTime.now().minusHours(4), getPage());
        assertEquals(1, bookings.size());
        assertEquals(booking, bookings.get(0));
    }

    @Test
    public void findByBookerAndStatusTest() {
        booking.setStatus(BookingStatus.WAITING);
        persistBooking();
        List<Booking> bookings = repository.findByBookerAndStatus(user2.getId(),
                BookingStatus.WAITING, getPage());

        assertEquals(1, bookings.size());
        assertEquals(booking, bookings.get(0));
    }

    @Test
    public void findByItemOwnerIdOrderByStartDescTest() {
        persistBooking();
        List<Booking> bookings = repository.findByItemOwnerIdOrderByStartDesc(user.getId(), getPage());

        assertEquals(1, bookings.size());
        assertEquals(booking, bookings.get(0));
    }

    @Test
    public void findByItemOwnerCurrentTest() {
        persistBooking();
        List<Booking> bookings = repository.findByItemOwnerCurrent(user.getId(),
                LocalDateTime.now().minusHours(2), getPage());

        assertEquals(1, bookings.size());
        assertEquals(booking, bookings.get(0));
    }

    @Test
    public void findByItemOwnerPastTest() {
        persistBooking();
        List<Booking> bookings = repository.findByItemOwnerPast(user.getId(),
                LocalDateTime.now().plusHours(2), getPage());

        assertEquals(1, bookings.size());
        assertEquals(booking, bookings.get(0));
    }

    @Test
    public void findByItemOwnerFutureTest() {
        persistBooking();
        List<Booking> bookings = repository.findByItemOwnerFuture(user.getId(),
                LocalDateTime.now().minusHours(4), getPage());

        assertEquals(1, bookings.size());
        assertEquals(booking, bookings.get(0));
    }

    @Test
    public void findByItemOwnerAndStatusTest() {
        booking.setStatus(BookingStatus.WAITING);
        persistBooking();
        List<Booking> bookings = repository.findByItemOwnerAndStatus(user.getId(),
                BookingStatus.WAITING, getPage());

        assertEquals(1, bookings.size());
        assertEquals(booking, bookings.get(0));
    }

    @Test
    public void findByBookerIdAndItemIdAndEndBeforeTest() {
        persistBooking();
        Booking foundBooking = repository.findByBookerIdAndItemIdAndEndBefore(user2.getId(),
                item.getId(), LocalDateTime.now().plusHours(1)).orElseThrow();
        assertEquals(booking, foundBooking);
    }

    @Test
    public void findBookingsLastTest() {
        persistBooking();
        Booking booking1 = booking;
        booking1.setEnd(LocalDateTime.now().minusHours(6));
        booking1.setStart(LocalDateTime.now().minusHours(5));
        testEntityManager.persist(booking1);

        List<Booking> bookings = repository.findBookingsLast(List.of(item.getId()),
                LocalDateTime.now(), user.getId());

        assertEquals(1, bookings.size());
        assertEquals(booking, bookings.get(0));
    }

    @Test
    public void findBookingsNextTest() {
        booking.setEnd(LocalDateTime.now().minusHours(7));
        booking.setStart(LocalDateTime.now().minusHours(6));
        persistBooking();
        Booking booking1 = booking;
        booking.setEnd(LocalDateTime.now().plusHours(4));
        booking.setStart(LocalDateTime.now().plusHours(3));
        testEntityManager.persist(booking1);

        List<Booking> bookings = repository.findBookingsNext(List.of(item.getId()),
                LocalDateTime.now(), user.getId());

        assertEquals(1, bookings.size());
        assertEquals(booking1, bookings.get(0));

    }

}
