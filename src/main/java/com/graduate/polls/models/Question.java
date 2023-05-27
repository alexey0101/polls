package com.graduate.polls.models;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;
import java.util.List;

@Entity
@Data
@Table(name = "question")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "question_id")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
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

    @NotNull(message = "Question type cannot be null")
    @JsonProperty("question_type")
    @Enumerated(EnumType.STRING)
    private QuestionType questionType;

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
    @Size(min = 1, max = 255, message = "Question must contain at least one answer and no more than 255 answers")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<AnswerOption> answers;

    @JsonProperty("next_question_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIdentityReference(alwaysAsId = true)
    private Question nextQuestion;

    @Column(name = "scale_min")
    @Min(value = -1000, message = "Scale min must be greater than or equal to -1000")
    @JsonProperty("scale_min")
    private Long scaleMin;

    @Column(name = "scale_max")
    @Max(value = 1000, message = "Scale max must be less than or equal to 1000")
    @JsonProperty("scale_max")
    private Long scaleMax;
}
