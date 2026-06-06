package com.logicq1.crm.modules.lead.controller;

import com.logicq1.crm.modules.contacts.services.ContactService;
import com.logicq1.crm.modules.email.dto.MailBody;
import com.logicq1.crm.modules.lead.dto.CreateLeadRequest;
import com.logicq1.crm.modules.lead.dto.UpdateLeadRequest;
import com.logicq1.crm.modules.lead.service.LeadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/leads")
public class LeadController {

    private final LeadService leadService;
    private final ContactService contactService;

    public LeadController(LeadService leadService, ContactService contactService) {
        this.leadService = leadService;
        this.contactService = contactService;
    }


    //create
    @PostMapping
    public ResponseEntity<?> createLead(@RequestBody CreateLeadRequest request){
        return leadService.createLead(request);
    }

    //update
    @PutMapping
    public ResponseEntity<?> updateLead(@RequestBody UpdateLeadRequest request){
        return leadService.updateLead(request);
    }

    //delete
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLead(@PathVariable("id") UUID leadId){
        return leadService.deleteLead(leadId);
    }

    //get all
    @GetMapping
    public ResponseEntity<?> listAllLeads(){
        return leadService.listAllLeads();
    }

    //get by id
    @GetMapping("/{id}")
    public ResponseEntity<?> getLeadById(@PathVariable("id") UUID leadId) {
        return leadService.getLeadById(leadId);
    }

    //convert from lead to contact
    @PostMapping("/{id}/convert-to-contact")
    public ResponseEntity<String> convertToContact(@PathVariable("id") UUID leadId) {
        return contactService.convertLeadToContact(leadId);
    }

    //send mail
    @PostMapping("/send-mail")
    public ResponseEntity<String> sendMailToLead(@RequestBody MailBody emailContent) {
        return leadService.sendMailToLead(emailContent);
    }

    //timeline




}
