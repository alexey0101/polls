package com.graduate.polls.models;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;

@Data
@Entity
@Table(name = "answer_option")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "answer_id")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AnswerOption implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    @JsonProperty("answer_id")
    private Long id;

    @Column
    @NotBlank(message = "Answer text cannot be blank")
    @Size(max = 255, message = "Answer text cannot be longer than 255 characters")
    @JsonProperty("answer_text")
    private String answerText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    @NotNull
    @JsonIgnore
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "next_question_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @NotNull
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("next_question_id")
    private Question nextQuestion;
}
