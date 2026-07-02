package com.academicsaas.academic.domain.model;

import com.academicsaas.academic.domain.model.valueobject.Score;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Grade {

    private final UUID id;
    private final UUID evaluationId;
    private final UUID studentId;
    private Score score;
    private String comments;
    private final UUID gradedBy;
    private final Instant gradedAt;
    private final Instant createdAt;
    private Instant updatedAt;

    public Grade(UUID id, UUID evaluationId, UUID studentId, Score score,
                 String comments, UUID gradedBy, Instant gradedAt,
                 Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id);
        this.evaluationId = Objects.requireNonNull(evaluationId);
        this.studentId = Objects.requireNonNull(studentId);
        this.score = Objects.requireNonNull(score);
        this.comments = comments;
        this.gradedBy = Objects.requireNonNull(gradedBy);
        this.gradedAt = Objects.requireNonNull(gradedAt);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = Objects.requireNonNull(updatedAt);
    }

    public static Grade create(UUID id, UUID evaluationId, UUID studentId, Score score, UUID gradedBy) {
        var now = Instant.now();
        return new Grade(id, evaluationId, studentId, score, null, gradedBy, now, now, now);
    }

    public UUID getId() { return id; }
    public UUID getEvaluationId() { return evaluationId; }
    public UUID getStudentId() { return studentId; }
    public Score getScore() { return score; }
    public String getComments() { return comments; }
    public UUID getGradedBy() { return gradedBy; }
    public Instant getGradedAt() { return gradedAt; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Grade grade)) {
            return false;
        }
        return id.equals(grade.id);
    }

    @Override
    public int hashCode() { return id.hashCode(); }
}
