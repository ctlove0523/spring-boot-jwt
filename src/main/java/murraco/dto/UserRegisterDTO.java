package murraco.dto;

import lombok.Getter;
import lombok.Setter;
import murraco.model.Role;

import java.util.List;

/**
 * Copyright (C), 2018-2019, open source
 * FileName: UserRegisterDTO
 *
 * @author: chentong
 * Date:     2019/6/3 21:07
 */
@Getter
@Setter
public class UserRegisterDTO {
    private String username;
    private String password;
    private String email;
    private String phone;
    private List<Role> roles;
}
