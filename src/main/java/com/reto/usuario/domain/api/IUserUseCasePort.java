package com.reto.usuario.domain.api;

import com.reto.usuario.application.dto.response.UserCustomerResponseDto;
import com.reto.usuario.domain.model.UserModel;

public interface IUserUseCasePort {

    UserModel registerUserWithOwnerRole(UserModel userModel);

    void registerUserWithEmployeeRole(UserModel userModel);

    UserModel findUserByEmail(String email);

    UserModel getUserById(Long idUser);

    UserModel registerUserWithCustomerRol(UserModel userModel);
}