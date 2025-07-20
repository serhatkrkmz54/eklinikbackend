package com.eklinik.eklinikapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.eklinik.eklinikapi.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByNationalId(username)
                .orElseThrow(() -> new UsernameNotFoundException("Bu TC Kimlik numarasıyla kullanıcı bulunamadı: " + username));

    }
}
