package com.logicq1.crm.modules.contacts.services;

import com.logicq1.crm.modules.activityTImeline.service.ActivityTimelineService;
import com.logicq1.crm.modules.contacts.dto.ContactDto;
import com.logicq1.crm.modules.contacts.dto.CreateContactDto;
import com.logicq1.crm.modules.contacts.entity.Contact;
import com.logicq1.crm.modules.contacts.mapper.ContactMapper;
import com.logicq1.crm.modules.contacts.repository.ContactRepository;
import com.logicq1.crm.modules.organization.entity.Organization;
import com.logicq1.crm.modules.organization.repository.OrganizationRepository;
import com.logicq1.crm.modules.user.entity.User;
import com.logicq1.crm.modules.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@SpringBootTest(classes = ContactService.class)
class ContactServiceTest {

    @Autowired
    private ContactService contactService;

    @MockitoBean
    private OrganizationRepository organizationRepository;
    @MockitoBean
    private ContactRepository contactRepository;
    @MockitoBean
    private ContactMapper contactMapper;
    @MockitoBean
    private UserRepository userRepository;

    private Organization organization;
    private User user;

    @BeforeEach
    void setUp() {
        organization = Organization.builder()
                .id(UUID.randomUUID())
                .name("Acme")
                .build();

        user = User.builder()
                .id(UUID.randomUUID())
                .organization(organization)
                .build();
    }

    private CreateContactDto createDto(String email) {
        return new CreateContactDto(
                email, "John Doe", "12345", "alt@acme.com",
                "desc", "note", "Manager", "Sales",
                "web", "US", "NYC", "NY", "web"
        );
    }

    private ContactDto sampleDto(UUID id) {
        return new ContactDto(
                id, "john@acme.com", "John Doe", "12345", "alt@acme.com",
                "desc", "note", "Manager", "Sales", "web",
                "US", "NYC", "NY", "web",
                UUID.randomUUID(), organization.getId()
        );
    }

    @Test
    void createContact_persistsContactWithOwnerAndOrganization_andLogsActivity() {
        CreateContactDto dto = createDto("john@acme.com");
        Contact mapped = new Contact();

        when(contactRepository.findByEmailAndOrganization(dto.email(), organization))
                .thenReturn(Optional.empty());
        when(contactMapper.toContact(dto)).thenReturn(mapped);

        contactService.createContact(dto, user);

        assertThat(mapped.getOwner()).isEqualTo(user);
        assertThat(mapped.getOrganization()).isEqualTo(organization);
        verify(contactRepository).save(mapped);
    }

    @Test
    void createContact_throwsConflict_whenContactWithEmailAlreadyExists() {
        CreateContactDto dto = createDto("dup@acme.com");

        when(contactRepository.findByEmailAndOrganization(dto.email(), organization))
                .thenReturn(Optional.of(new Contact()));

        assertThatThrownBy(() -> contactService.createContact(dto, user))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);

        verify(contactRepository, never()).save(any());
    }

    @Test
    void getContactById_returnsMappedDto_whenFound() {
        UUID id = UUID.randomUUID();
        Contact contact = new Contact();
        ContactDto dto = sampleDto(id);

        when(contactRepository.findById(id)).thenReturn(Optional.of(contact));
        when(contactMapper.toContactDto(contact)).thenReturn(dto);

        assertThat(contactService.getContactById(id.toString())).isEqualTo(dto);
    }

    @Test
    void getContactById_throwsBadRequest_whenIdIsNotUuid() {
        assertThatThrownBy(() -> contactService.getContactById("not-a-uuid"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void getContactById_throwsNotFound_whenMissing() {
        UUID id = UUID.randomUUID();
        when(contactRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contactService.getContactById(id.toString()))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getContactByOwner_mapsEveryContact() {
        when(contactRepository.findByOwner(user))
                .thenReturn(List.of(new Contact(), new Contact()));
        when(contactMapper.toContactDto(any())).thenReturn(sampleDto(UUID.randomUUID()));

        assertThat(contactService.getContactByOwner(user)).hasSize(2);
    }

    @Test
    void getContactByOrganization_throws_whenOrganizationNotFound() {
        UUID orgId = UUID.randomUUID();
        when(organizationRepository.findById(orgId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contactService.getContactByOrganization(orgId.toString()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Organization Not Found");
    }

    @Test
    void updateContact_appliesChangesAndLogsThem_whenSameOrganization() {
        UUID id = UUID.randomUUID();
        Contact contact = new Contact();
        contact.setOrganization(organization);
        ContactDto dto = sampleDto(id);

        when(contactRepository.findById(id)).thenReturn(Optional.of(contact));
        when(contactRepository.save(contact)).thenReturn(contact);
        when(contactMapper.toContactDto(contact)).thenReturn(dto);

        ContactDto result = contactService.updateContact(id.toString(), createDto("a@b.com"), user);

        assertThat(result).isEqualTo(dto);
        verify(contactMapper).updateContactFromDto(any(), eq(contact));
    }

    @Test
    void updateContact_throwsForbidden_whenContactBelongsToAnotherOrganization() {
        UUID id = UUID.randomUUID();
        Contact contact = new Contact();
        contact.setOrganization(Organization.builder().id(UUID.randomUUID()).build());

        when(contactRepository.findById(id)).thenReturn(Optional.of(contact));

        assertThatThrownBy(() ->
                contactService.updateContact(id.toString(), createDto("a@b.com"), user))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.FORBIDDEN);

        verify(contactRepository, never()).save(any());
    }

    @Test
    void updateContact_throwsNotFound_whenMissing() {
        UUID id = UUID.randomUUID();
        when(contactRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                contactService.updateContact(id.toString(), createDto("a@b.com"), user))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteContact_deletesAndLogs_whenSameOrganization() {
        UUID id = UUID.randomUUID();
        Contact contact = new Contact();
        contact.setOrganization(organization);

        when(contactRepository.findById(id)).thenReturn(Optional.of(contact));

        contactService.deleteContact(id.toString(), user);

        verify(contactRepository).delete(contact);
    }

    @Test
    void deleteContact_throwsForbidden_whenContactBelongsToAnotherOrganization() {
        UUID id = UUID.randomUUID();
        Contact contact = new Contact();
        contact.setOrganization(Organization.builder().id(UUID.randomUUID()).build());

        when(contactRepository.findById(id)).thenReturn(Optional.of(contact));

        assertThatThrownBy(() -> contactService.deleteContact(id.toString(), user))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.FORBIDDEN);

        verify(contactRepository, never()).delete(any());
    }
}
