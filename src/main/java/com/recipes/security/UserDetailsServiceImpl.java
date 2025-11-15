package com.recipes.security;

import com.recipes.entities.Chef;
import com.recipes.repositories.ChefRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    ChefRepository chefRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Chef chef = chefRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Chef Not Found with email: " + email));

        return UserDetailsImpl.build(chef);
    }
}