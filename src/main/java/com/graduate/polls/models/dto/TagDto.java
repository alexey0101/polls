package com.graduate.polls.models.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TagDto {
    @NotBlank(message = "Tag name cannot be blank")
    @Size(min = 3, max = 20, message = "Tag name cannot be longer than 20 characters")
    String name;
}
