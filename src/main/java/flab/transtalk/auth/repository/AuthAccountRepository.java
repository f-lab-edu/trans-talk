package flab.transtalk.auth.repository;

import flab.transtalk.auth.domain.AuthAccount;
import flab.transtalk.auth.domain.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthAccountRepository extends JpaRepository<AuthAccount, Long> {
    Optional<AuthAccount> findByProviderAndProviderId(AuthProvider provider, String providerId);
}
