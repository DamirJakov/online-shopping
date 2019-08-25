package pl.codementors.finalproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.codementors.finalproject.model.LocalUser;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocalUserRepository extends JpaRepository<LocalUser, String> {

    Optional<LocalUser> findOneByUsername(String userName);
    Optional<LocalUser> findOneByEmail(String email);
    Optional<LocalUser> findOneByName(String name);
    List<LocalUser> findAll();


}
