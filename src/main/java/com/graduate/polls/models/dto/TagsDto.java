package com.graduate.polls.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class TagsDto {
    @Size(min = 1, message = "tags list should not be empty!")
    @JsonProperty("tag_ids")
    @NotNull(message = "tags list should not be null!")
    private List<Long> tags;
}
