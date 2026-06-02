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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ContactService {
    private OrganizationRepository organizationRepository;
    private ContactRepository contactRepository;
    private ContactMapper contactMapper;
    private UserRepository userRepository;

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

            contactRepository.saveAll(contacts);

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

}
