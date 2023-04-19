package com.graduate.polls.models;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;
import java.util.List;

@Entity
@Data
@Table(name = "question")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "question_id")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Question implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Column(name = "question_id")
    @NotNull(message = "Question id cannot be null")
    @Positive(message = "Question id must be positive")
    @JsonProperty("question_id")
    private Long questionId;

    @Column(name = "question_text")
    @NotBlank(message = "Question text cannot be blank")
    @Size(max = 255, message = "Question text cannot be longer than 255 characters")
    @JsonProperty("question_text")
    private String questionText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_id")
    @JsonIgnore
    private Poll poll;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @NotNull(message = "Question answers cannot be null")
    @Size(min = 1, max = 255, message = "Question must contain at least one answer and no more than 255 answers")
    private List<AnswerOption> answers;
}
