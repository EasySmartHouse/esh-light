package net.easysmarthouse.service;

import net.easysmarthouse.shared.service.UserService;
import net.easysmarthouse.util.RequestHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
public class ESHUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Autowired
    private HttpServletRequest request;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String ip = RequestHelper.getClientIP(request);
        if (loginAttemptService.isBlocked(ip)) {
            throw new RuntimeException("blocked");
        }

        UserDetails userDetails = userService.findByUsername(username);
        if (userDetails == null) {
            throw new UsernameNotFoundException("No user found");
        }

        if (!userDetails.isEnabled()) {
            throw new RuntimeException("User is disabled");
        }

        return userDetails;
    }

}
