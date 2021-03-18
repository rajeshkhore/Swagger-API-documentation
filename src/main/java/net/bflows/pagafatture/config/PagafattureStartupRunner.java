package net.bflows.pagafatture.config;

import java.util.ArrayList;
import java.util.List;

import net.bflows.pagafatture.entities.RoleEntity;
import net.bflows.pagafatture.repositories.RoleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({ "test" })
public class PagafattureStartupRunner implements CommandLineRunner {

    @Autowired
    RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {

        // Initializing role table data on application start up for test profile.

        RoleEntity roleAdmin = new RoleEntity(1l, "ROLE_ADMIN", "Admin role");
        RoleEntity roleUser = new RoleEntity(2l, "ROLE_CUSTOMER", "Customer role");
        RoleEntity roleAn = new RoleEntity(3l, "ROLE_ANONYMOUS", "Anonymous role");

        List<RoleEntity> roles = new ArrayList<RoleEntity>();
        roles.add(roleAdmin);
        roles.add(roleUser);
        roles.add(roleAn);

        roleRepository.saveAll(roles);

    }

}
