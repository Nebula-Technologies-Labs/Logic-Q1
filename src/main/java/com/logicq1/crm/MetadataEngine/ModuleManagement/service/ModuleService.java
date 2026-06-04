package com.logicq1.crm.MetadataEngine.ModuleManagement.service;

import com.logicq1.crm.MetadataEngine.ModuleManagement.dto.ModuleInput;
import com.logicq1.crm.MetadataEngine.ModuleManagement.entity.Module;
import com.logicq1.crm.MetadataEngine.ModuleManagement.repository.ModuleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ModuleService {

    private final ModuleRepository moduleRepository;

    public Module createModule(ModuleInput input) {
        Module module = Module.builder()
                .organizationId(input.getOrganizationId())
                .name(input.getName())
                .apiName(input.getApiName())
                .description(input.getDescription())
                .icon(input.getIcon())
                .isBuiltIn(input.getIsBuiltIn())
                .isActive(input.getIsActive())
                .createdBy(input.getCreatedBy())
                .build();
        return moduleRepository.save(module);
    }

    public Module updateModule(UUID id, ModuleInput input) {
        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Module not found: " + id));
        // update mutable fields
        if (input.getOrganizationId() != null) module.setOrganizationId(input.getOrganizationId());
        if (input.getName() != null) module.setName(input.getName());
        if (input.getApiName() != null) module.setApiName(input.getApiName());
        if (input.getDescription() != null) module.setDescription(input.getDescription());
        if (input.getIcon() != null) module.setIcon(input.getIcon());
        if (input.getIsBuiltIn() != null) module.setIsBuiltIn(input.getIsBuiltIn());
        if (input.getIsActive() != null) module.setIsActive(input.getIsActive());

        return moduleRepository.save(module);
    }

    @Transactional(readOnly = true)
    public Module getModule(UUID id) {
        return moduleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Module not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<Module> listModules() {
        return moduleRepository.findAll();
    }

    public boolean deleteModule(UUID id) {
        if (!moduleRepository.existsById(id)) {
            throw new EntityNotFoundException("Module not found: " + id);
        }
        moduleRepository.deleteById(id);
        return true;
    }
}