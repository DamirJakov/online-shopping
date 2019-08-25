package pl.codementors.finalproject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.codementors.finalproject.model.*;
import pl.codementors.finalproject.repository.LocalUserRepository;
import pl.codementors.finalproject.repository.ProductRepository;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class LocalUserController {

    @Autowired
    private LocalUserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("")
    public String listOfUsers(Model model) {
        if (LoginUserInfo.isUserHasRole(UserRole.USER)) {
            model.addAttribute("users", Optional.ofNullable(userRepository.findOne(LoginUserInfo.getUserID())).map(Arrays::asList).orElseGet(ArrayList::new));
        } else if (LoginUserInfo.isUserHasRole(UserRole.ADMIN)) {
            model.addAttribute("users", userRepository.findAll());
        }
        model.addAttribute("currentUser", LoginUserInfo.getUser());

        return "user/user";
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("user", new LocalUser());
        return "user/addUser";
    }

    @PostMapping("/add")
    public String addUser(@ModelAttribute("user") @Valid LocalUser user, BindingResult errors) {
        if (user.getId() == null) {

            Optional<LocalUser> isUsedUsername = userRepository.findOneByUsername(user.getUsername());
            if (isUsedUsername.isPresent()) {
                errors.rejectValue("username", "username.alreadytaken");
                return "user/addUser";
            }

            Optional<LocalUser> isUsedEmail = userRepository.findOneByEmail(user.getEmail());
            if (isUsedEmail.isPresent()) {
                errors.rejectValue("email", "email.alreadytaken");
                return "user/addUser";
            }

            user.setId(Generate.Id());
            final BCryptPasswordEncoder cryptPasswordEncoder = new BCryptPasswordEncoder();
            user.setPassword(cryptPasswordEncoder.encode(user.getPassword()));
            user.setActive(true);
            userRepository.save(user);
        } else {

            Optional<LocalUser> isUsedUsername = userRepository.findOneByUsername(user.getUsername());
            if (isUsedUsername.isPresent()) {
                if (!isUsedUsername.get().getId().equals(user.getId())) {
                    errors.rejectValue("username", "username.alreadytaken");
                    return "user/addUser";
                }

            }

            Optional<LocalUser> isUsedEmail = userRepository.findOneByEmail(user.getEmail());
            if (isUsedEmail.isPresent()) {
                if (!isUsedEmail.get().getId().equals(user.getId())) {
                    errors.rejectValue("email", "email.alreadytaken");
                    return "user/addUser";
                }
            }

            LocalUser localUser = new LocalUser();
            localUser.setId(Generate.Id());
            localUser.setActive(user.isActive());
            localUser.setName(user.getName());
            localUser.setSurname(user.getSurname());
            localUser.setUsername(user.getUsername());
            localUser.setEmail(user.getEmail());
            localUser.setActive(true);
            localUser.setRole(UserRole.USER);
            if (!StringUtils.isEmpty(user.getPassword())) {
                final BCryptPasswordEncoder cryptPasswordEncoder = new BCryptPasswordEncoder();
                localUser.setPassword(cryptPasswordEncoder.encode(user.getPassword()));
            }

            LocalUser userEntity = userRepository.save(localUser);


        }
        return "redirect:/user";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute("user") LocalUser user, Model model) {

        boolean userUpdated = userValidateAndUpdate(user.getId(),model, user);
        if (userUpdated) {
            return "redirect:/user";
        }
        return "error";
    }

    @GetMapping("/update/{id}")
    public String updateUser(@PathVariable String id, Model model) {
        if (LoginUserInfo.getUserID().equals(id) || LoginUserInfo.isUserHasRole(UserRole.ADMIN)) {
            LocalUser user = userRepository.findOne(id);
            model.addAttribute("user", user);
            return "user/addUser";
        } else {
            return "error";
        }
    }

    public boolean userValidateAndUpdate(String id, Model model, LocalUser user) {
        if (LoginUserInfo.isUserHasRole(UserRole.USER)) {
            if (id.equals(LoginUserInfo.getUserID())) {
                editUser(user);
                model.addAttribute("user", userRepository.findOne(id));
            } else {
                return false;
            }
        } else if (LoginUserInfo.isUserHasRole(UserRole.ADMIN)) {
            LocalUser localUser = userRepository.findOne(id);
            if (!localUser.getRole().equals(UserRole.ADMIN)) {
                editUser(user);

                model.addAttribute("user", userRepository.findOne(id));
            } else {
                if (id.equals(LoginUserInfo.getUserID())) {
                    editUser(user);

                    model.addAttribute("user", userRepository.findOne(id));
                } else {
                    return false;
                }
            }

        }
        return true;
    }

    private void editUser(LocalUser user) {
        LocalUser fromDB = userRepository.findOne(user.getId());
        fromDB.setName(user.getName());
        fromDB.setSurname(user.getSurname());
        fromDB.setUsername(user.getUsername());
        fromDB.setEmail(user.getEmail());
        if (!StringUtils.isEmpty(user.getPassword().trim())) {
            final BCryptPasswordEncoder cryptPasswordEncoder = new BCryptPasswordEncoder();
            fromDB.setPassword(cryptPasswordEncoder.encode(user.getPassword()));
        }
        userRepository.save(fromDB);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        boolean userDeleted = userDelete(id);
        if(userDeleted){
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @GetMapping("/delete/{id}")
    @Transactional
    public String deleteUser(@PathVariable String id) {
        if (LoginUserInfo.getUserID().equals(id) || LoginUserInfo.isUserHasRole(UserRole.ADMIN)) {
            productRepository.removeUserFromProductAndSetAvailable(id);
            userDelete(id);
            return "redirect:/user";
        } else {
            return "error";
        }
    }

    private boolean userDelete(String id) {
        if (LoginUserInfo.isUserHasRole(UserRole.USER)) {
            if (id.equals(LoginUserInfo.getUserID())) {
                productRepository.removeUserFromProductAndSetAvailable(id);
                userRepository.delete(LoginUserInfo.getUserID());
                SecurityContextHolder.clearContext();
            } else {
                return false;
            }
        } else if (LoginUserInfo.isUserHasRole(UserRole.ADMIN)) {
            LocalUser user = userRepository.findOne(id);
            if (!user.getRole().equals(UserRole.ADMIN)) {
                userRepository.delete(id);
            } else {
                if (id.equals(LoginUserInfo.getUserID())) {
                    userRepository.delete(id);
                    SecurityContextHolder.clearContext();
                } else {
                    return false;
                }
            }
        }
        return true;
    }
}
