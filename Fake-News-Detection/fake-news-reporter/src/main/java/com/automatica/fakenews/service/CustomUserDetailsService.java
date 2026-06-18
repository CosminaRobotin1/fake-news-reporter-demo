package com.automatica.fakenews.service;

import com.automatica.fakenews.model.User;
import com.automatica.fakenews.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
// Serviciu folosit de Spring Security pentru incarcarea
// utilizatorilor din baza de date in timpul autentificarii.
@Service
public class CustomUserDetailsService implements UserDetailsService {

    // Repository folosit pentru cautarea utilizatorilor in baza de date
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)// Cauta utilizatorul dupa username in tabela users
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Construieste obiectul UserDetails necesar Spring Security
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isEnabled(),
                true,
                true,
                true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()))// Lista rolurilor utilizatorului
        );
    }
}
