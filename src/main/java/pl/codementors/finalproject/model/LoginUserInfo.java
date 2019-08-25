package pl.codementors.finalproject.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.StringUtils;
import pl.codementors.finalproject.security.LocalUserPrincipal;

import java.util.Collection;

public class LoginUserInfo {

    public static String getUsername() {
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        if (principal instanceof LocalUserPrincipal) {
            return ((LocalUserPrincipal) principal).getUsername();
        } else {
            return principal.toString();
        }
    }

    public static String getUserID() {
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        if (principal instanceof LocalUserPrincipal) {
            return ((LocalUserPrincipal) principal).getLoginUser()
                    .getId();
        }
        return "";
    }
    public static LocalUser getUser(){
    Object principal = SecurityContextHolder.getContext()
            .getAuthentication()
            .getPrincipal();
        if (principal instanceof LocalUserPrincipal) {
        return ((LocalUserPrincipal) principal).getLoginUser();
    } else {
        return null;
    }
}

    public static boolean isUserHasRole(UserRole role){
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        if (principal instanceof LocalUserPrincipal) {
            return role == ((LocalUserPrincipal) principal).getLoginUser().getRole();
        }
        return false;
    }


}