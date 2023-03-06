package ru.practicum.shareit.requests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.TestHelper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class RequestTest {
    @Autowired
    private JacksonTester<Request> json;
    TestHelper test = new TestHelper();


    @Test
    public void testRequest() throws Exception {
        User user = test.getUser();
        Request request = new Request(1L, user, "description", LocalDateTime.now());
        JsonContent<Request> result = json.write(request);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.requester.name").isEqualTo(test.getUser().getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
    }
}
