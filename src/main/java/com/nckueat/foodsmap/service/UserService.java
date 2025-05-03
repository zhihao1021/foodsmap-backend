package com.nckueat.foodsmap.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nckueat.foodsmap.exception.UserNotFound;
import com.nckueat.foodsmap.model.enitiy.User;
import com.nckueat.foodsmap.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFound(id.toString()));
        return user;
    }
}
