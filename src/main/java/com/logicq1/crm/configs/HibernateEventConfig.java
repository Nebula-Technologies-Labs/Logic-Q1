package com.logicq1.crm.configs;

import com.logicq1.crm.modules.audit.utils.AuditEventListener;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import lombok.RequiredArgsConstructor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class HibernateEventConfig {

    @PersistenceUnit
    private EntityManagerFactory emf;

    private final AuditEventListener auditEventListener;

    @PostConstruct
    public void registerListeners() {

        SessionFactoryImpl sessionFactory =
                emf.unwrap(SessionFactoryImpl.class);

        EventListenerRegistry registry =
                sessionFactory.getServiceRegistry()
                        .getService(EventListenerRegistry.class);

        assert registry != null;
        registry.appendListeners(
                EventType.POST_INSERT,
                auditEventListener
        );

        registry.appendListeners(
                EventType.POST_UPDATE,
                auditEventListener
        );

        registry.appendListeners(
                EventType.POST_DELETE,
                auditEventListener
        );
    }
}
