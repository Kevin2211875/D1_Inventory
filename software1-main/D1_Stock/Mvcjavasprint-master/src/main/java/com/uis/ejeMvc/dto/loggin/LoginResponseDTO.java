package com.uis.ejeMvc.dto.loggin;

import com.uis.ejeMvc.dto.security.PerfilIdpDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LoginResponseDTO {

    private final String accessToken;
    private final String refreshToken;
    private final Long expiresIn;
    private final String tokenType;
    private final PerfilIdpDTO perfil;
}
