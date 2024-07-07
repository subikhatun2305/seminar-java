package com.dreamsol.securities;

import com.dreamsol.entites.User;
import com.dreamsol.repositories.UserRepository;
import com.dreamsol.services.impl.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService
{
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        User user = userRepository.findByEmail(username);
        if(user==null)
        {
            throw new UsernameNotFoundException("User with username: [" + username + "] not found!");
        }
        return new UserDetailsImpl(user);
    }
}
