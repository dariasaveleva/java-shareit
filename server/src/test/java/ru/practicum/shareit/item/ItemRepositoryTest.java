package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.practicum.shareit.TestHelper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase
@RunWith(SpringRunner.class)
class ItemRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private ItemRepository repository;
    TestHelper test = new TestHelper();
    User user = test.getUser1();
    Item item = new Item(
        null,
        "name zero",
        "description zero",
        true,
        user,
        null);

    Item item1 = new Item(
            null,
            "name one",
            "description one",
            true,
            user,
            null);

    @Test
    public void contextLoads() {
        assertNotNull(testEntityManager);
    }

    @Test
    public void findAllByOwnerIdOrderByIdAscTest() {
        testEntityManager.persist(user);
        testEntityManager.persist(item1);
        testEntityManager.persist(item);

        PageRequest page = PageRequest.of(0,20);

        List<Item> items = repository.findAllByOwnerIdOrderByIdAsc(user.getId(), page);

        assertEquals(item1, items.get(0));
        assertEquals(item, items.get(1));
    }

    @Test
    public void searchByTextTest() {
        testEntityManager.persist(user);
        testEntityManager.persist(item1);
        testEntityManager.persist(item);
        PageRequest page = PageRequest.of(0,20);
        List<Item> items = repository.searchByText("zero", page);
        assertEquals(1, items.size());
        assertEquals("name zero", items.get(0).getName());

    }

    @Test
    public void searchByRequestIdTest() {
        Request request = new Request();
        request.setId(1L);
        request.setDescription("request description");
        request.setRequester(user);
        request.setCreated(LocalDateTime.now());
        Item newItem = new Item(null, "name3", "description3", true, user, request);
        testEntityManager.persist(user);
        testEntityManager.persist(item1);
        testEntityManager.persist(item);
        testEntityManager.persist(newItem);

        List<Item> items = repository.searchByRequestId(List.of(newItem.getRequest().getId()));
        assertEquals(1, items.size());
        assertEquals("name3", items.get(0).getName());
        assertEquals("description3", items.get(0).getDescription());
    }

    @Test
    public void searchByItemRequestIdTest() {
        Request request = new Request();
        request.setId(1L);
        request.setDescription("request description");
        request.setRequester(user);
        request.setCreated(LocalDateTime.now());
        Item newItem = new Item(null, "name3", "description3", true, user, request);
        testEntityManager.persist(user);
        testEntityManager.persist(item1);
        testEntityManager.persist(item);
        testEntityManager.persist(newItem);
        List<Item> items = repository.searchByItemRequestId(1L);
        assertEquals("name3", items.get(0).getName());
        assertEquals("description3", items.get(0).getDescription());
    }
}
