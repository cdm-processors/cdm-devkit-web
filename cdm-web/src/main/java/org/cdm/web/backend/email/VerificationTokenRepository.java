package org.cdm.web.backend.email;

import org.cdm.web.backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository
        extends JpaRepository<VerificationToken, Long> {

    VerificationToken findByToken(String token);

    VerificationToken findByUsername(String username);
}
