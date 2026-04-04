package com.FMS.dashboard.dto;

import com.FMS.dashboard.model.Role;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserDTO {
    private String name;
    private String email;
    private String password;
    private Role role;
}