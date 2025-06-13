package flab.transtalk.auth.security.principal;

import lombok.Builder;
import lombok.Value;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;

@Value
@Builder
public class CustomUserDetails implements UserDetails {

    private final Long userId;                          // userId
    private final Collection<? extends GrantedAuthority> authorities;

    public Long getUserId(){
        return userId;
    }

    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override public String getPassword() { return ""; }      // 소셜 로그인
    @Override public String getUsername() { return String.valueOf(getUserId()); }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
