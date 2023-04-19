package com.graduate.polls.models;

import com.ethlo.time.DateTime;
import com.fasterxml.jackson.annotation.*;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "poll")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Poll implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_id")
    @NotNull
    @JsonIgnore
    private App app;

    @Column
    @JsonProperty("user_id")
    @Size(max = 255, message = "User ID cannot be longer than 255 characters")
    private String userId;

    @Column
    @NotBlank(message = "Poll name cannot be blank")
    @Size(max = 255, message = "Poll name cannot be longer than 255 characters")
    private String name;

    @Column
    @NotBlank(message = "Poll description cannot be blank")
    @Size(max = 255, message = "Poll description cannot be longer than 255 characters")
    private String description;

    @Column(name = "created_at")
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @Column
    @NotNull
    private boolean active = true;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "start_question_id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("start_question_id")
    private Question startQuestion;

    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @NotNull
    private List<Question> questions;

    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private List<UserResponse> userResponses;
}
