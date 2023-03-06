package ru.practicum.shareit.requests;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.practicum.shareit.TestHelper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase
@RunWith(SpringRunner.class)
class RequestRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private RequestRepository requestRepository;
    TestHelper test = new TestHelper();

    User user = test.getUser1();
    Request request = new Request(null, user, "description", LocalDateTime.now());



    @Test
    public void contextLoads() {
        assertNotNull(testEntityManager);
    }

    @Test
    public void findAllByRequesterIdTest() {
        testEntityManager.persist(user);
        testEntityManager.persist(request);

        List<Request> requestsList = requestRepository.findAllByRequesterId(user.getId());
        assertEquals(1, requestsList.size());
        assertEquals("description", requestsList.get(0).getDescription());
    }

    @Test
    public void findAllPageableTest() {
        testEntityManager.persist(user);
        testEntityManager.persist(request);
        PageRequest page = PageRequest.of(0, 10);

        List<Request> requestsList = requestRepository.findAllPageable(9999L, page);
        assertEquals(1, requestsList.size());
        assertEquals("description", requestsList.get(0).getDescription());

        requestsList = requestRepository.findAllPageable(user.getId(), page);
        assertTrue(requestsList.isEmpty());
    }
}
