package com.vamshi.stockflow_backend.category.domain;

import com.vamshi.stockflow_backend.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "categories")
public class Category extends BaseEntity {

    // Uniqueness for active (non-deleted) categories is enforced at the service
    // layer via existsByNameIgnoreCaseAndDeletedFalse. A DB-level unique constraint
    // would reject re-creating a name that belongs to a soft-deleted category.
    @Column(nullable = false)
    private String name;

}
