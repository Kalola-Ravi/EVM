package com.ems.events.repo;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ems.events.entity.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByContactNumber(String contactNumber);
    Optional<User> findByEmail(String email);
    Optional<User> findByUserName(String userName);
}
