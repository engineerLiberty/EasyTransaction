package com.example.money_way.service.impl;

import com.example.money_way.configuration.mail.EmailService;
import com.example.money_way.configuration.security.JwtUtils;
import com.example.money_way.dto.request.SignUpDto;
import com.example.money_way.dto.response.ApiResponse;
import com.example.money_way.exception.ValidationException;
import com.example.money_way.model.User;
import com.example.money_way.repository.UserRepository;
import com.example.money_way.service.UserService;
import com.example.money_way.utils.AppUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImplementation implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AppUtil appUtils;
    private final EmailService emailService;
    private final UserRepository userRepository;

    @Override
    public ResponseEntity<ApiResponse> signUp(SignUpDto signUpDto) throws ValidationException {

        if(!(appUtils.isValidEmail(signUpDto.getEmail())))
            throw new ValidationException("Email is not valid");

        if(!(signUpDto.getConfirmPassword().equals(signUpDto.getPassword())))
            throw new ValidationException("Passwords do not match");

        if(signUpDto.getPin().length() != 4)
            throw new ValidationException("Pin must be a four digit number");

        Boolean isUserExist = userRepository.existsByEmail(signUpDto.getEmail());

        if (isUserExist)
            throw new ValidationException("User Already Exists!");

        User user = new User();
        user.setFirstName(signUpDto.getFirstName());
        user.setLastName(signUpDto.getLastName());
        user.setEmail(signUpDto.getEmail());
        user.setPhoneNumber(signUpDto.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));
        user.setBvn(signUpDto.getBvn());
        user.setPin(signUpDto.getPin());
        String token = jwtUtils.generateSignUpConfirmationToken(signUpDto.getEmail());
        user.setIsActive(false);
        userRepository.save(user);

        String link = "Hello "  + signUpDto.getFirstName()  +
                "Copy the token below to activate your account: " + token;
        String subject = "MoneyWay";

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(signUpDto.getEmail());
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(link);
        emailService.sendEmail(signUpDto.getEmail(),subject, simpleMailMessage.getText());

        return ResponseEntity.ok(new ApiResponse<>("Successful", "SignUp Successful. Check your mail to activate your account", null));
    }
}
