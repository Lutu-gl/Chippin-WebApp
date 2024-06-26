package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserChangePasswordDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserRegisterDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.debt.DebtGroupDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.group.GroupListDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.entity.Recipe;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.UserAlreadyExistsException;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepr.groupphase.backend.service.DebtService;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserRepository userRepository;
    private final DebtService debtService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenizer jwtTokenizer;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        LOGGER.trace("Load all user by email");
        try {
            ApplicationUser applicationUser = findApplicationUserByEmail(email);

            List<GrantedAuthority> grantedAuthorities;
            if (applicationUser.getAdmin()) {
                grantedAuthorities = AuthorityUtils.createAuthorityList("ROLE_ADMIN", "ROLE_USER");
            } else {
                grantedAuthorities = AuthorityUtils.createAuthorityList("ROLE_USER");
            }

            return new User(applicationUser.getEmail(), applicationUser.getPassword(), grantedAuthorities);
        } catch (NotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage(), e);
        }
    }

    @Override
    public ApplicationUser findApplicationUserByEmail(String email) {
        LOGGER.trace("Find application user by email");
        ApplicationUser applicationUser = userRepository.findByEmail(email);
        if (applicationUser != null) {
            return applicationUser;
        }
        throw new NotFoundException(String.format("Could not find the user with the email address %s", email));
    }

    @Override
    public String login(UserLoginDto userLoginDto) {
        LOGGER.trace("login({})", userLoginDto);
        UserDetails userDetails = loadUserByUsername(userLoginDto.getEmail());
        if (userDetails != null
            && userDetails.isAccountNonExpired()
            && userDetails.isAccountNonLocked()
            && userDetails.isCredentialsNonExpired()
            && passwordEncoder.matches(userLoginDto.getPassword(), userDetails.getPassword())
        ) {
            List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
            return jwtTokenizer.getAuthToken(userDetails.getUsername(), roles);
        }
        throw new BadCredentialsException("Username or password is incorrect or account is locked");
    }

    @Override
    public String register(UserRegisterDto userRegisterDto, boolean admin) throws UserAlreadyExistsException {
        LOGGER.trace("register({}, {})", userRegisterDto, admin); // password is not logged here

        ApplicationUser existingUser = userRepository.findByEmail(userRegisterDto.getEmail());
        if (existingUser != null) {
            throw new UserAlreadyExistsException("User with given email already exists");
        }
        ApplicationUser applicationUser =
            ApplicationUser.builder().email(userRegisterDto.getEmail()).password(passwordEncoder.encode(userRegisterDto.getPassword())).admin(admin).build();
        userRepository.save(applicationUser);
        UserDetails userDetails = loadUserByUsername(userRegisterDto.getEmail());
        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        return jwtTokenizer.getAuthToken(userDetails.getUsername(), roles);
    }

    @Override
    @Transactional
    public void changePassword(UserChangePasswordDto changePasswordDto, String username) {
        LOGGER.trace("changePassword({})", changePasswordDto);
        ApplicationUser applicationUser = userRepository.findByEmail(username);
        if (applicationUser == null) {
            throw new NotFoundException("User not found");
        }
        if (!passwordEncoder.matches(changePasswordDto.getCurrentPassword(), applicationUser.getPassword())) {
            throw new BadCredentialsException("Old password is incorrect");
        }
        applicationUser.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
        userRepository.save(applicationUser);
    }

    @Override
    public Set<GroupEntity> getGroupsByUserEmail(String email) {
        LOGGER.trace("getGroupsByUserEmail({})", email);
        return userRepository.findGroupsByUserEmail(email);
    }

    @Override
    public Set<GroupListDto> getGroupsByUserEmailWithDebtInfos(String email) {
        LOGGER.trace("getGroupsByUserEmailWithDebtInfos({})", email);
        Set<GroupListDto> groupsByUserEmailWithDebtInfos = new HashSet<>();

        Set<GroupEntity> groupsByUserEmail = userRepository.findGroupsByUserEmail(email);
        for (GroupEntity group : groupsByUserEmail) {
            DebtGroupDetailDto byId = debtService.getById(email, group.getId());

            groupsByUserEmailWithDebtInfos.add(GroupListDto.builder()
                .id(group.getId())
                .groupName(group.getGroupName())
                .membersCount((long) group.getUsers().size())
                .membersDebts(byId.getMembersDebts())
                .build());
        }

        return groupsByUserEmailWithDebtInfos;
    }

    @Override
    public List<Recipe> getRecipesByUserEmail(String email) {
        LOGGER.trace("getRecipesByUserEmail({})", email);
        return userRepository.findRecipesByUserEmail(email);
    }


    @PostConstruct
    private void init() {
        LOGGER.info("Creating default users");
        UserRegisterDto user = UserRegisterDto.builder().email("user@email.com").password("password").build();
        UserRegisterDto admin = UserRegisterDto.builder().email("admin@email.com").password("password").build();
        try {
            register(user, false);
            register(admin, true);
        } catch (UserAlreadyExistsException e) {
            LOGGER.info("Default users already exist");
        }
    }


}
