package com.logicq1.crm.modules.contacts.services;

import com.logicq1.crm.modules.contacts.dto.ContactDto;
import com.logicq1.crm.modules.contacts.dto.CreateContactDto;
import com.logicq1.crm.modules.contacts.entity.Contact;
import com.logicq1.crm.modules.contacts.mapper.ContactMapper;
import com.logicq1.crm.modules.contacts.repository.ContactRepository;
import com.logicq1.crm.modules.organization.entity.Organization;
import com.logicq1.crm.modules.organization.repository.OrganizationRepository;
import com.logicq1.crm.modules.user.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ContactService {
    private OrganizationRepository organizationRepository;
    private ContactRepository contactRepository;
    private ContactMapper contactMapper;

    public void createContact(CreateContactDto data, User user){
        Contact contact = contactMapper.toContact(data);
        contact.setOwner(user);
        contact.setOrganization(user.getOrganization());

        contactRepository.save(contact);
    }

    public List<ContactDto> getContactByOwner(User owner){
        List<Contact> contacts = contactRepository.findByOwner(owner);
        return contacts.stream().map(contactMapper::toContactDto).toList();
    }

    public List<ContactDto> getContactByOrganization(String orgId){
        Organization organization = organizationRepository.findById(orgId).orElseThrow(() -> new RuntimeException("Organization Not Found"));
        List<Contact> contacts = contactRepository.findByOrganization(organization);
        return contacts.stream().map(contactMapper::toContactDto).toList();
    }

    public ContactDto getContactById(String id){
        Contact contact = contactRepository.findById(id).orElseThrow(() -> new RuntimeException("Contact not found"));
        return contactMapper.toContactDto(contact);
    }

}
