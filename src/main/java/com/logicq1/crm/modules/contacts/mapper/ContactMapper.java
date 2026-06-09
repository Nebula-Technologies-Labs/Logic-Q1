package com.logicq1.crm.modules.contacts.mapper;

import com.logicq1.crm.modules.contacts.dto.ContactDto;
import com.logicq1.crm.modules.contacts.dto.CreateContactDto;
import com.logicq1.crm.modules.contacts.entity.Contact;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ContactMapper {

    Contact toContact(CreateContactDto contactDto);

    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "organizationId", source = "organization.id")
    ContactDto toContactDto(Contact contact);

    @BeanMapping(
            nullValuePropertyMappingStrategy =
                    NullValuePropertyMappingStrategy.IGNORE
    )
    void updateContactFromDto(
            CreateContactDto data,
            @MappingTarget Contact contact
    );
}
