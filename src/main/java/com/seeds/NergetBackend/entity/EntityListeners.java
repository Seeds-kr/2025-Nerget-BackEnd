package com.seeds.NergetBackend.entity;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

public @interface EntityListeners {
    Class<AuditingEntityListener> value();
}
