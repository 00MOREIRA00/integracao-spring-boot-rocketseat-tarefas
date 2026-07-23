package br.com.rneto.tarefas.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.rneto.tarefas.user.UserModel;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/create")
    public UserModel createUser(@RequestBody UserModel userModel) {

        var userCreated = this.userRepository.save(userModel);
        return userModel;
    }
}
