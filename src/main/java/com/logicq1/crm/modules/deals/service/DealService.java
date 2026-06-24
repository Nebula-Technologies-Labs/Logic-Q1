package com.logicq1.crm.modules.deals.service;

import com.logicq1.crm.modules.contacts.entity.Contact;
import com.logicq1.crm.modules.contacts.repository.ContactRepository;
import com.logicq1.crm.modules.deals.dto.CreateDealDto;
import com.logicq1.crm.modules.deals.dto.DealDto;
import com.logicq1.crm.modules.deals.dto.UpdateDealDto;
import com.logicq1.crm.modules.deals.entity.Deal;
import com.logicq1.crm.modules.deals.mapper.DealMapper;
import com.logicq1.crm.modules.deals.repository.DealRepository;
import com.logicq1.crm.modules.user.entity.User;
import com.logicq1.crm.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DealService {

    private final DealRepository dealRepository;
    private final DealMapper dealMapper;
    private final ContactRepository contactRepository;
    private final UserRepository userRepository;

    public DealDto createDeal(CreateDealDto request, User user) {

        if (dealRepository.existsByDealNameIgnoreCaseAndOrganizationId(
                request.getDealName(),
                user.getOrganization().getId()
        )) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A deal with this name already exists"
            );
        }

        Deal deal = dealMapper.toEntity(request);

        if (request.getContactId() != null) {
            Contact contact = contactRepository.findById(
                    request.getContactId()
            ).orElseThrow(() ->
                    new RuntimeException("Contact not found"));

            deal.setContact(contact);
        }



        deal.setOwner(user);
        deal.setOrganization(user.getOrganization());
        deal.setCreatedBy(user);

        Deal savedDeal = dealRepository.save(deal);

        return dealMapper.toDTO(savedDeal);
    }

    @Transactional(readOnly = true)
    public DealDto getDealById(UUID id ,User user) {

        Deal deal = dealRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Deal not found"));

        validateOrganizationAccess(deal, user);

        return dealMapper.toDTO(deal);
    }

    @Transactional(readOnly = true)
    public List<DealDto> getAllDeals(UUID organizationId) {

        return dealMapper.toDTO(
                dealRepository.findByOrganizationId(organizationId)
        );
    }

    public DealDto updateDeal(
            UUID id,
            UpdateDealDto request,
            User user
    ) {

        Deal deal = dealRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Deal not found"));

        validateOrganizationAccess(deal, user);

        dealMapper.updateDealFromDto(request, deal);

        if (request.getContactId() != null) {

            Contact contact = contactRepository.findById(
                    request.getContactId()
            ).orElseThrow(() ->
                    new RuntimeException("Contact not found"));

            deal.setContact(contact);
        }

        if (request.getOwnerId() != null) {

            User owner = userRepository.findById(
                    request.getOwnerId()
            ).orElseThrow(() ->
                    new RuntimeException("Owner not found"));

            deal.setOwner(owner);
        }

        Deal updatedDeal = dealRepository.save(deal);

        return dealMapper.toDTO(updatedDeal);
    }

    public void deleteDeal(UUID id,User user) {

        Deal deal = dealRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Deal not found"));

        validateOrganizationAccess(deal, user);

        dealRepository.delete(deal);
    }
    private void validateOrganizationAccess(Deal deal, User user) {

        if (!deal.getOrganization().getId()
                .equals(user.getOrganization().getId())) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You do not have access to this deal"
            );
        }
    }
}