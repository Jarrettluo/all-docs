package com.jiaruiblog.service.impl;

import com.jiaruiblog.entity.User;
import com.jiaruiblog.entity.dto.RegistryUserDTO;
import com.jiaruiblog.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testRegistry() {
        RegistryUserDTO userDTO = new RegistryUserDTO();
        userDTO.setUsername("test");
        userDTO.setPassword("123456");
        userDTO.setMail("test@test.com");
        userDTO.setPhone("12345678901");
        userDTO.setNickname("test");

        when(userRepository.findByUsername(userDTO.getUsername())).thenReturn(null);
        when(userRepository.insert(any(User.class))).thenReturn(1);

        userService.registry(userDTO);
        // 可加断言校验行为
    }
} 