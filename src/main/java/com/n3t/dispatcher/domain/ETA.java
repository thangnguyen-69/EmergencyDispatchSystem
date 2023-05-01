package com.n3t.dispatcher.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.Specification;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "eta")
@Builder
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ETA implements Serializable {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "emergency_id", referencedColumnName = "id", updatable = false)
    @Getter
    @Setter
    private Emergency emergency;

    @Column(name = "status", columnDefinition = "TEXT DEFAULT 'DRAFT'", nullable = false)
    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    private Status status;

    @Column(name = "current_timestamp", columnDefinition = "TIMESTAMP")
    @Getter
    @Setter
    private LocalDateTime time;

    public ETA() {
        this.status = Status.PENDING;
        this.time = LocalDateTime.now();
    }

    public ETA(Emergency emergency) {
        this();
        this.emergency = emergency;
    }

    public static class Specs {

        public static Specification<ETA> filterByCount(Integer count) {
            return (root, query, cb) -> {
                if (Objects.isNull(count)) {
                    return cb.conjunction();
                }
                return cb.lessThanOrEqualTo(root.get(String.valueOf(ETA_.count)), count);
            };
        }

        public static Specification<ETA> filterByStatus(Status status) {
            return (root, query, cb) -> {
                if (Objects.isNull(status)) {
                    return cb.conjunction();
                }
                return cb.equal(root.get(String.valueOf(ETA_.status)), status);
            };
        }

        public static Specification<ETA> filterById(Long id) {
            return (root, query, cb) -> {
                if (Objects.isNull(id)) {
                    return cb.conjunction();
                }
                return cb.equal(root.get(String.valueOf(ETA_.id)), id);
            };
        }
    }


    public enum Status {
        FAILED,
        PENDING,
        SUCCESS
    }
}
