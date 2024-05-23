package com.arboviroses.conectaDengue.Domain.Repositories.Users;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.arboviroses.conectaDengue.Domain.Entities.RefreshToken;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Integer> {
    Optional<RefreshToken> findByToken(String token);   
}