package btvn.it211_project.repository;

import btvn.it211_project.domain.Role;
import btvn.it211_project.domain.UserAccount;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    boolean existsByEmail(String email);

    Optional<UserAccount> findByEmail(String email);

    Page<UserAccount> findByRoleAndActive(Role role, boolean active, Pageable pageable);

    Page<UserAccount> findByRole(Role role, Pageable pageable);

    Page<UserAccount> findByActive(boolean active, Pageable pageable);

    Page<UserAccount> findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String fullName, String email, Pageable pageable);
}