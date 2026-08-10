package br.com.rneto.tarefas.task;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Generated;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.boot.convert.Delimiter;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity(name = "tb_task")
public class TaskModel {
    /**
     * ID
     * Usuário
     * Descrição
     * Titulo
     * Data Inicio
     * Data Termino
     * Prioridade
     */
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID uuid;
    private String description;

    @Column(length = 50)
    private String title;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private String priority;
    private UUID idUser;

    @CreationTimestamp()
    private LocalDateTime createdAt;



}
