package com.automatica.fakenews.config;

import com.automatica.fakenews.model.User;
import com.automatica.fakenews.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
//se executa automat la pornirea aplicatiei
// si creeaza un utilizator administrator implicit daca nu exista deja.
@Component
public class DataInitializer implements CommandLineRunner {

    // Repository folosit pentru accesarea tabelei users din baza de date
    @Autowired
    private UserRepository userRepository;

    // Obiect folosit pentru criptarea (hash-uirea) parolelor
    @Autowired
    private PasswordEncoder passwordEncoder;

    // Metoda run() este apelata automat de Spring dupa pornirea aplicatiei
    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            admin.setEnabled(true);
            userRepository.save(admin);
            System.out.println("Default admin user created with username: admin");
        }
    }
}
