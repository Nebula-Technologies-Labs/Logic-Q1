package com.logicq1.crm.modules.contacts.repository;

import com.logicq1.crm.modules.contacts.entity.Contact;
import com.logicq1.crm.modules.organization.entity.Organization;
import com.logicq1.crm.modules.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContactRepository extends JpaRepository<Contact, UUID> {
    List<Contact> findByOwner(User owner);

    List<Contact> findByOrganization(Organization organization);
}
