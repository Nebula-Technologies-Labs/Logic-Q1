package com.logicq1.crm.MetadataEngine.ModuleManagement.controller;

import com.logicq1.crm.MetadataEngine.ModuleManagement.dto.ModuleInput;
import com.logicq1.crm.MetadataEngine.ModuleManagement.entity.Module;
import com.logicq1.crm.MetadataEngine.ModuleManagement.service.ModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class ModuleController {

    private final ModuleService moduleService;

    @MutationMapping(name = "createModule")
    public Module createModule(@Argument("input") ModuleInput input) {
        return moduleService.createModule(input);
    }

    @MutationMapping(name = "updateModule")
    public Module updateModule(@Argument("id") String id, @Argument("input") ModuleInput input) {
        UUID uuid = UUID.fromString(id);
        return moduleService.updateModule(uuid, input);
    }

    @QueryMapping(name = "getModule")
    public Module getModule(@Argument("id") String id) {
        UUID uuid = UUID.fromString(id);
        return moduleService.getModule(uuid);
    }

    @QueryMapping(name = "listModules")
    public List<Module> listModules() {
        return moduleService.listModules();
    }

    @MutationMapping(name = "deleteModule")
    public Boolean deleteModule(@Argument("id") String id) {
        UUID uuid = UUID.fromString(id);
        return moduleService.deleteModule(uuid);
    }
}