package br.com.rneto.tarefas.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.yaml.snakeyaml.events.Event;

import java.util.UUID;

public interface UserRepository extends JpaRepository<UserModel, UUID> {



}
