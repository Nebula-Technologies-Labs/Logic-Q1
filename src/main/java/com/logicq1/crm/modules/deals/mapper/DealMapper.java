package com.logicq1.crm.modules.deals.mapper;

import com.logicq1.crm.modules.deals.dto.CreateDealDto;
import com.logicq1.crm.modules.deals.dto.DealDto;
import com.logicq1.crm.modules.deals.dto.UpdateDealDto;
import com.logicq1.crm.modules.deals.entity.Deal;
import com.logicq1.crm.modules.user.entity.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DealMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "contact", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Deal toEntity(CreateDealDto request);

    @BeanMapping(
            nullValuePropertyMappingStrategy =
                    NullValuePropertyMappingStrategy.IGNORE
    )
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "contact", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateDealFromDto(
            UpdateDealDto request,
            @MappingTarget Deal deal
    );

    @Mapping(target = "contactId", source = "contact.id")
    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "ownerName", expression = "java(getFullName(deal.getOwner()))")
    @Mapping(target = "organizationId", source = "organization.id")
    @Mapping(target = "createdBy", source = "createdBy.id")
    DealDto toDTO(Deal deal);

    List<DealDto> toDTO(List<Deal> deals);

    default String getFullName(User user) {
        if (user == null) {
            return null;
        }

        return user.getFirstName() + " " + user.getLastName();
    }
}