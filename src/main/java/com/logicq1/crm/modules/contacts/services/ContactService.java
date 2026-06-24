package com.logicq1.crm.modules.contacts.services;

import com.logicq1.crm.modules.contacts.dto.ContactDto;
import com.logicq1.crm.modules.contacts.dto.CreateContactDto;
import com.logicq1.crm.modules.contacts.entity.Contact;
import com.logicq1.crm.modules.contacts.mapper.ContactMapper;
import com.logicq1.crm.modules.contacts.repository.ContactRepository;
import com.logicq1.crm.modules.organization.entity.Organization;
import com.logicq1.crm.modules.organization.repository.OrganizationRepository;
import com.logicq1.crm.modules.user.entity.User;
import com.logicq1.crm.modules.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ContactService {
    private OrganizationRepository organizationRepository;
    private ContactRepository contactRepository;
    private ContactMapper contactMapper;
    private UserRepository userRepository;

    public void createContact(CreateContactDto data, User user) {
        if (contactRepository.findByEmailAndOrganization(
                data.email(),
                user.getOrganization()
        ).isPresent()) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Contact already exists in your organization"
            );
        }
        Contact contact = contactMapper.toContact(data);
        contact.setOwner(user);
        contact.setOrganization(user.getOrganization());

        contactRepository.save(contact);
    }

    public List<ContactDto> getContactByOwner(User owner) {
        List<Contact> contacts = contactRepository.findByOwner(owner);
        return contacts.stream().map(contactMapper::toContactDto).toList();
    }

    public List<ContactDto> getContactByOrganization(String orgId) {
        Organization organization = organizationRepository.findById(UUID.fromString(orgId)).orElseThrow(() -> new RuntimeException("Organization Not Found"));
        List<Contact> contacts = contactRepository.findByOrganization(organization);
        return contacts.stream().map(contactMapper::toContactDto).toList();
    }

    public ContactDto getContactById(String id) {
        UUID contactId;
        try {
            contactId = UUID.fromString(id);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid contact id: '" + id + "' is not a valid UUID");
        }
        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));
        return contactMapper.toContactDto(contact);
    }

    @Transactional
    public void importContacts(MultipartFile file, String email) {

        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Organization organization = owner.getOrganization();

        List<Contact> contacts = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);

                if (row == null) {
                    continue;
                }

                String contactEmail = getCellValue(row.getCell(1));

                if (contactEmail == null || contactEmail.isBlank()) {
                    continue;
                }

                Contact contact = new Contact();

                contact.setName(getCellValue(row.getCell(0)));
                contact.setEmail(contactEmail);
                contact.setSecondaryEmail(getCellValue(row.getCell(2)));
                contact.setPhoneNumber(getCellValue(row.getCell(3)));
                contact.setDescription(getCellValue(row.getCell(4)));
                contact.setNote(getCellValue(row.getCell(5)));
                contact.setDepartment(getCellValue(row.getCell(6)));
                contact.setContactSource(getCellValue(row.getCell(7)));
                contact.setTitle(getCellValue(row.getCell(8)));
                contact.setAddress(getCellValue(row.getCell(9)));
                contact.setCountry(getCellValue(row.getCell(10)));
                contact.setState(getCellValue(row.getCell(11)));
                contact.setCity(getCellValue(row.getCell(12)));

                contact.setOwner(owner);
                contact.setOrganization(organization);

                contacts.add(contact);
            }

            List<Contact> savedContacts = contactRepository.saveAll(contacts);

        } catch (Exception e) {
            throw new RuntimeException("Failed to import contacts", e);
        }
    }

    private String getCellValue(Cell cell) {

        if (cell == null) {
            return null;
        }

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> null;
        };
    }



    public byte[] exportContacts(Organization organization) {

        List<Contact> contacts =
                contactRepository.findByOrganization(
                        organization
                );

        try (
                ByteArrayOutputStream outputStream =
                        new ByteArrayOutputStream()
        ) {

            SXSSFWorkbook workbook =
                    new SXSSFWorkbook(500);

            try {

                Sheet sheet =
                        workbook.createSheet("Contacts");

                String[] headers = {
                        "Name",
                        "Email",
                        "Secondary Email",
                        "Phone Number",
                        "Description",
                        "Note",
                        "Department",
                        "Contact Source",
                        "Title",
                        "Address",
                        "Country",
                        "State",
                        "City"
                };

                Row headerRow =
                        sheet.createRow(0);

                for (int i = 0; i < headers.length; i++) {

                    headerRow.createCell(i)
                            .setCellValue(headers[i]);
                }

                int rowNumber = 1;

                for (Contact contact : contacts) {

                    Row row =
                            sheet.createRow(rowNumber++);

                    row.createCell(0).setCellValue(nullSafe(contact.getName()));
                    row.createCell(1).setCellValue(nullSafe(contact.getEmail()));
                    row.createCell(2).setCellValue(nullSafe(contact.getSecondaryEmail()));
                    row.createCell(3).setCellValue(nullSafe(contact.getPhoneNumber()));
                    row.createCell(4).setCellValue(nullSafe(contact.getDescription()));
                    row.createCell(5).setCellValue(nullSafe(contact.getNote()));
                    row.createCell(6).setCellValue(nullSafe(contact.getDepartment()));
                    row.createCell(7).setCellValue(nullSafe(contact.getContactSource()));
                    row.createCell(8).setCellValue(nullSafe(contact.getTitle()));
                    row.createCell(9).setCellValue(nullSafe(contact.getAddress()));
                    row.createCell(10).setCellValue(nullSafe(contact.getCountry()));
                    row.createCell(11).setCellValue(nullSafe(contact.getState()));
                    row.createCell(12).setCellValue(nullSafe(contact.getCity()));
                }

                workbook.write(outputStream);

                return outputStream.toByteArray();

            } finally {

                workbook.dispose();
                workbook.close();
            }

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to export contacts",
                    e
            );
        }
    }

    private String nullSafe(String value) {
        return value == null ? "" : value;
    }

    public ContactDto updateContact(
            String id,
            CreateContactDto data,
            User user
    ) {

        UUID contactId = UUID.fromString(id);

        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Contact not found"
                        ));

        validateOrganizationAccess(contact, user);

        contactMapper.updateContactFromDto(data, contact);


        contact = contactRepository.save(contact);

        return contactMapper.toContactDto(contact);
    }
    public void deleteContact(String id, User user) {

        UUID contactId = UUID.fromString(id);

        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Contact not found"
                        ));
        validateOrganizationAccess(contact, user);


        contactRepository.delete(contact);
    }

    private void validateOrganizationAccess(Contact contact, User user) {

        if (!contact.getOrganization().getId()
                .equals(user.getOrganization().getId())) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You do not have access to this contact"
            );
        }
    }


    public ResponseEntity<String> convertLeadToContact(UUID leadId) {
        // This method would contain logic to convert a lead to a contact.
        // It would typically involve fetching the lead by ID, creating a new contact based on the lead's information,
        // saving the contact, and then updating the lead's status to "converted" or similar.
        // For now, we'll just return a placeholder response.



        return ResponseEntity.ok("Lead with ID " + leadId + " converted to contact successfully");
    }
}
