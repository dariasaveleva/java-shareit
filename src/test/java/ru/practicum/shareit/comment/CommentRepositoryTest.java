package ru.practicum.shareit.comment;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import ru.practicum.shareit.TestHelper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase
@RunWith(SpringRunner.class)
class CommentRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private CommentRepository repository;
    TestHelper test = new TestHelper();
    User user = test.getUser1();
    Comment comment = test.getComment();
    Item item = test.getItem1();

    @Test
    public void contextLoads() {
        assertNotNull(testEntityManager);
    }

    @Test
    public void findAllByCommentsTest() {
        testEntityManager.persist(user);
        testEntityManager.persist(item);
        testEntityManager.persist(comment);
        List<Long> itemsId = List.of(item.getId());

        List<Comment> comments = repository.findAllByComments(itemsId);

        assertEquals(1, comments.size());
        assertEquals("comment", comments.get(0).getText());
    }
}
