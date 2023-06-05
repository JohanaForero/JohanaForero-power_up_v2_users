package com.reto.usuario.domain.spi;

import com.reto.usuario.domain.model.UserModel;

public interface IUserPersistenceDomainPort {

    UserModel saveUser(UserModel userModel);

    UserModel findByEmail(String email);

    boolean existsByEmail(String email);

    UserModel findById(Long idUser);
}
