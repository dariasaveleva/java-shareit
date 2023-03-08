package ru.practicum.shareit.comment.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentDto {
     Long id;
     @NotNull(groups = Create.class)
     @NotBlank(groups = Create.class)
     @Size(min = 10, max = 240)
     String text;
     String authorName;
     LocalDateTime created;

}
