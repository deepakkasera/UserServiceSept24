package com.example.userservicesept24.services;

import com.example.userservicesept24.dtos.SendEmailDto;
import com.example.userservicesept24.models.Token;
import com.example.userservicesept24.models.User;
import com.example.userservicesept24.repositories.TokenRepository;
import com.example.userservicesept24.repositories.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@Service
public class UserService {
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private TokenRepository tokenRepository;
    private KafkaTemplate<String, String> kafkaTemplate;
    private ObjectMapper objectMapper;

    public UserService(UserRepository userRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder,
                       TokenRepository tokenRepository,
                       KafkaTemplate kafkaTemplate,
                       ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.tokenRepository = tokenRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public Token login(String email, String password) {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User with email: " + email + " not found in the DB.");
        }

        User user = optionalUser.get();

        if (bCryptPasswordEncoder.matches(password, user.getHashedPassword())) {
            //Generate the token.
            Token token = createToken(user);
            Token savedToken = tokenRepository.save(token);


            return savedToken;
        }

        return null;
    }

    public User signUp(String name,
                       String email,
                       String password) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);

        //First encrypt the password using BCrypt Algorithm before storing into the DB.
        user.setHashedPassword(bCryptPasswordEncoder.encode(password));

        // Push a sendEmail event to Kafka to send a welcome email to the user.

        SendEmailDto emailDto = new SendEmailDto();
        emailDto.setTo(email);
        emailDto.setSubject("Welcome to Scaler");
        emailDto.setBody("We are happy to have you on out platform.");

        try {
            System.out.println("Pushing the event inside Kafka.");
            kafkaTemplate.send(
                    "sendEmail",
                    objectMapper.writeValueAsString(emailDto)
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return userRepository.save(user);
    }

    public void logout(String tokenValue) {
        Optional<Token> optionalToken = tokenRepository.findByValueAndDeletedAndExpiryAtGreaterThan(
                tokenValue,
                false,
                new Date()
        );

        if (optionalToken.isEmpty()) {
            //throw TokenInvalidException
            return;
        }

        Token token = optionalToken.get();

        token.setDeleted(true);
        tokenRepository.save(token);
    }

    public User validateToken(String tokenValue) {
        //First find out that the token with the value is present in the DB or not.
        //Expiry time of the token > current time and deleted should be false.
        Optional<Token> optionalToken = tokenRepository.findByValueAndDeletedAndExpiryAtGreaterThan(
                tokenValue,
                false,
                new Date() // currentTime
        );

        if (optionalToken.isEmpty()) {
            //token is invalid;
            return null;
        }

        return optionalToken.get().getUser();
    }

    private Token createToken(User user) {
        Token token = new Token();
        token.setUser(user);

        token.setValue(RandomStringUtils.randomAlphanumeric(128));

        //Expiry time of the token is let's say 30 days from now.
        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysAfterCurrentTime = today.plusDays(30);

        Date expiryAt = Date.from(thirtyDaysAfterCurrentTime.atStartOfDay(ZoneId.systemDefault()).toInstant());
        token.setExpiryAt(expiryAt);

        return token;
    }
}
