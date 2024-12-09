package com.example.userservicedec24.repositories;

import com.example.userservicedec24.models.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long>  {
    Token save(Token token);

    Optional<Token> findByValue(String value);
}
