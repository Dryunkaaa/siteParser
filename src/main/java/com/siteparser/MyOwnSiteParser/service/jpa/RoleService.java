package com.siteparser.MyOwnSiteParser.service.jpa;

import com.siteparser.MyOwnSiteParser.domain.Role;
import com.siteparser.MyOwnSiteParser.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public Role getRoleByName(String name) {
        return roleRepository.findByName(name);
    }

    public List<Role> getAll(){
        return roleRepository.findAll();
    }

    public void createRole(String roleName) {
        if (roleName != null && roleName != "") {
            Role role = new Role();
            role.setRole(roleName);
            roleRepository.save(role);
        }
    }
}
