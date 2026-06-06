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
    @RequestMapping("/create")
    public ResponseEntity<String> createLead(@RequestBody CreateLeadRequest request){
        return leadService.createLead(request);
    }

    //update
    @RequestMapping("/update")
    public ResponseEntity<String> updateLead(@RequestBody UpdateLeadRequest request){
        return leadService.updateLead(request);
    }

    //delete
    @RequestMapping("/delete/{id}")
    public ResponseEntity<String> deleteLead(@PathVariable("id") UUID leadId){
        return leadService.deleteLead(leadId);
    }

    //get all
    @RequestMapping("/listAll")
    public ResponseEntity<?> listAllLeads(){
        return leadService.listAllLeads();
    }

    //get by id
    @RequestMapping("/get/{id}")
    public ResponseEntity<?> getLeadById(@PathVariable("id") UUID leadId) {
        return leadService.getLeadById(leadId);
    }

    //convert from lead to contact
    @RequestMapping("/convertToContact/{id}")
    public ResponseEntity<String> convertToContact(@PathVariable("id") UUID leadId) {
        return contactService.convertLeadToContact(leadId);
    }

    //send mail
    @RequestMapping("/sendMail")
    public ResponseEntity<String> sendMailToLead(@RequestBody MailBody emailContent) {
        return leadService.sendMailToLead(emailContent);
    }

    //timeline




}
