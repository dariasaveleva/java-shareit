package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.exception.ErrorResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
public class ErrorResponseTest {
    private final ErrorResponse  errorResponse = new ErrorResponse("Error", "Error happened, sorry");

    @Test
    public void setErrorResponseTest() {
        assertEquals(errorResponse.getError(), "Error");
        assertEquals(errorResponse.getDescription(), "Error happened, sorry");
    }


}
