package com.logicq1.crm.modules.lead.service;

import com.logicq1.crm.enums.LeadStatus;
import com.logicq1.crm.modules.auth.service.CurrentUserService;
import com.logicq1.crm.modules.email.dto.MailBody;
import com.logicq1.crm.modules.email.service.EmailService;
import com.logicq1.crm.modules.lead.LeadMapper;
import com.logicq1.crm.modules.lead.dto.CreateLeadRequest;
import com.logicq1.crm.modules.lead.dto.UpdateLeadRequest;
import com.logicq1.crm.modules.lead.entity.Lead;
import com.logicq1.crm.modules.lead.repository.LeadRepository;
import com.logicq1.crm.modules.organization.entity.Organization;
import com.logicq1.crm.modules.user.entity.User;
import com.logicq1.crm.modules.user.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class LeadService {

    private final LeadRepository leadRepository;
    private final LeadMapper leadMapper;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;
    private final EmailService emailService;


    public LeadService(LeadRepository leadRepository, LeadMapper leadMapper, UserRepository userRepository, CurrentUserService currentUserService, EmailService emailService) {
        this.leadRepository = leadRepository;
        this.leadMapper = leadMapper;
        this.userRepository = userRepository;
        this.currentUserService = currentUserService;
        this.emailService = emailService;
    }


    public ResponseEntity<String> createLead(CreateLeadRequest request) {

        if (leadRepository.findLeadByEmail(request.email())) {
            return ResponseEntity.badRequest()
                    .body("Lead with email " + request.email() + " already exists.");
        }

        User currentUser = currentUserService.getCurrentUser();
        Organization organization = currentUser.getOrganization();

        Lead lead = leadMapper.toEntity(request);

        if (request.ownerId() != null) {

            User owner = userRepository.findById(request.ownerId())
                    .orElseThrow(() ->
                            new RuntimeException("Owner not found"));

            lead.setOwner(owner);
        }

        // Organization
        lead.setOrganization(organization);

        // Audit
        lead.setCreatedBy(currentUser);
        lead.setUpdatedBy(currentUser);

        lead.setCreatedAt(LocalDateTime.now());
        lead.setUpdatedAt(LocalDateTime.now());

        // Conversion
        lead.setConverted(false);
        lead.setConvertedAt(null);
        lead.setConvertedBy(null);
        lead.setConvertedContactId(null);

        // Defaults
        if (lead.getEmailOptOut() == null) {
            lead.setEmailOptOut(false);
        }

        if (lead.getLeadStatus() == null) {
            lead.setLeadStatus(LeadStatus.NEW);
        }

        leadRepository.save(lead);

        return ResponseEntity.ok("Lead created successfully");
    }

    public ResponseEntity<String> updateLead(UpdateLeadRequest request) {
        Lead lead = leadRepository.findById(request.leadId())
                .orElseThrow(() -> new RuntimeException("Lead not found"));

        leadMapper.updateLeadFromDto(request, lead);

        User currentUser = currentUserService.getCurrentUser();

        lead.setUpdatedBy(currentUser);
        lead.setUpdatedAt(LocalDateTime.now());

        leadRepository.save(lead);

        return ResponseEntity.ok("Lead updated successfully");

    }

    public ResponseEntity<String> deleteLead(UUID leadId) {

        int deletedRows = leadRepository.deleteByIdAndReturnCount(leadId);

        if (deletedRows == 0) {
            return ResponseEntity.badRequest()
                    .body("Lead not found or already deleted");
        }

        return ResponseEntity.ok("Lead deleted successfully");
    }

    public ResponseEntity<?> listAllLeads() {
        return ResponseEntity.ok(leadRepository.findAll());
    }

    public ResponseEntity<?> getLeadById(UUID leadId) {
        return leadRepository.findById(leadId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.badRequest()
                                .body("Lead not found"));
    }


    public ResponseEntity<String> sendMailToLead(MailBody emailContent) {
        emailService.sendSimpleMessage(emailContent);
        return  ResponseEntity.ok("Email sent successfully");
    }
}
