package com.logicq1.crm.modules.contacts.controller;

import com.logicq1.crm.modules.contacts.dto.ContactDto;
import com.logicq1.crm.modules.contacts.dto.CreateContactDto;
import com.logicq1.crm.modules.contacts.entity.Contact;
import com.logicq1.crm.modules.contacts.services.ContactService;
import com.logicq1.crm.modules.user.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/contact")
@AllArgsConstructor
public class ContactController {

    private ContactService contactService;

    @PostMapping("/create")
    private ResponseEntity<Map<String,Object>> createContact(@AuthenticationPrincipal User user, @RequestBody CreateContactDto data){
        contactService.createContact(data,user);

        HashMap<String, Object> response = new HashMap<>();
        response.put("status" , true);
        response.put("message" , "Contact Created Successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/owner/")
    private ResponseEntity<Map<String, Object>> getContactByOwner(@AuthenticationPrincipal User user){
        List<ContactDto> contacts = contactService.getContactByOwner(user);
        HashMap<String, Object> response = new HashMap<>();
        response.put("status" , true);
        response.put("contacts" , contacts);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/org/{orgId}/")
    private ResponseEntity<Map<String, Object>> getContactByOrganization(@PathVariable String orgId){
        List<ContactDto> contacts = contactService.getContactByOrganization(orgId);
        HashMap<String, Object> response = new HashMap<>();
        response.put("status" , true);
        response.put("contacts" , contacts);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}")
    private ResponseEntity<Map<String, Object>> getContactById(@PathVariable String id){
        ContactDto contact = contactService.getContactById(id);
        HashMap<String, Object> response = new HashMap<>();
        response.put("status" , true);
        response.put("contact" , contact);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/import")
    public ResponseEntity<String> importContacts(
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) {
        contactService.importContacts(file, authentication.getName());

        return ResponseEntity.ok("Contacts imported successfully");
    }
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportContacts(
            @AuthenticationPrincipal User user
    ) {

        byte[] excelFile =
                contactService.exportContacts(
                        user.getOrganization()
                );

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=contacts.xlsx"
                )
                .contentType(
                        MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        )
                )
                .body(excelFile);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateContact(
            @PathVariable String id,
            @RequestBody CreateContactDto data,
            @AuthenticationPrincipal User user
    ) {

        ContactDto contact =
                contactService.updateContact(id, data, user);

        HashMap<String, Object> response = new HashMap<>();

        response.put("status", true);
        response.put("contact", contact);
        response.put("message", "Contact Updated Successfully");

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteContact(
            @PathVariable String id,
            @AuthenticationPrincipal User user
    ) {

        contactService.deleteContact(id, user);

        HashMap<String, Object> response = new HashMap<>();

        response.put("status", true);
        response.put("message", "Contact Deleted Successfully");

        return ResponseEntity.ok(response);
    }

}
