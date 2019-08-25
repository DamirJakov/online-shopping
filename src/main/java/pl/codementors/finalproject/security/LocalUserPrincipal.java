package pl.codementors.finalproject.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.codementors.finalproject.model.LocalUser;
import pl.codementors.finalproject.model.UserRole;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;


public class LocalUserPrincipal implements UserDetails {
    private LocalUser localUser;

    @java.beans.ConstructorProperties({"localUser"})
    public LocalUserPrincipal(LocalUser localUser) {
        this.localUser = localUser;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (localUser.getRole() == UserRole.ADMIN) {
            return Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
        } else if (localUser.getRole() == UserRole.USER) {
            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    @Override
    public String getPassword() {
        return localUser.getPassword();
    }

    @Override
    public String getUsername() {
        return localUser.getUsername();
    }

    public LocalUser getLoginUser() {
        return localUser;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return localUser.isActive();
    }
}
