package pl.codementors.finalproject.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.codementors.finalproject.FinalprojectApplication;
import pl.codementors.finalproject.model.LocalUser;
import pl.codementors.finalproject.repository.LocalUserRepository;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class LocalUserDetailsService implements UserDetailsService {
    private static final Logger LOGGER = Logger.getLogger(FinalprojectApplication.class.getName());

    @Autowired
    private LocalUserRepository localUserRepository;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        LOGGER.log(Level.INFO,"Login:"+userName);
        Optional<LocalUser> localUser = localUserRepository.findOneByUsername(userName);
        if (localUser.isPresent()) {

            LOGGER.log(Level.INFO,"Login:"+userName +" found in username");
            return new LocalUserPrincipal(localUser.get());
        }else {
            localUser = localUserRepository.findOneByEmail(userName);
            if (localUser.isPresent()) {
                LOGGER.log(Level.INFO,"Login:"+userName +" found in email");
                return new LocalUserPrincipal(localUser.get());
            }
        }
        LOGGER.log(Level.INFO,"Login:"+userName +" not found");
        throw new UsernameNotFoundException(String.format("%s was not found.", userName));
    }

}

