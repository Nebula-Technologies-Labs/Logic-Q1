package com.logicq1.crm.modules.contacts.mapper;

import com.logicq1.crm.modules.contacts.dto.ContactDto;
import com.logicq1.crm.modules.contacts.dto.CreateContactDto;
import com.logicq1.crm.modules.contacts.entity.Contact;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ContactMapper {

    Contact toContact(CreateContactDto contactDto);

    ContactDto toContactDto(Contact contact);
}
