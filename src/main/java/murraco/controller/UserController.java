package murraco.controller;

import murraco.dto.UserInfoResponseDTO;
import murraco.dto.UserRegisterDTO;
import murraco.model.User;
import murraco.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;


    @RequestMapping(value = "/signup", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> signup(@RequestBody UserRegisterDTO request) {
        User user = new User();
        BeanUtils.copyProperties(request, user);
        userService.signup(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{username}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserInfoResponseDTO> queryUserInfo(@PathVariable String username) {
        User user = userService.search(username);
        UserInfoResponseDTO response = new UserInfoResponseDTO();
        BeanUtils.copyProperties(user,response);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }


}
