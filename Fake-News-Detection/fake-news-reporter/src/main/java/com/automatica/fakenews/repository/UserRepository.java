package com.automatica.fakenews.repository;

import com.automatica.fakenews.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
// Repository folosit pentru accesarea utilizatorilor din tabela users.
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Returneaza Optional<User> deoarece utilizatorul poate exista sau nu
    Optional<User> findByUsername(String username);
}
//Prin: extends JpaRepository<User, Long>, primești automat:save()
//findById()
//findAll()
//deleteById()
//existsById()
//count()