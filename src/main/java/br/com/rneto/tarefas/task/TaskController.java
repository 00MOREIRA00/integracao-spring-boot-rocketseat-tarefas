package br.com.rneto.tarefas.task;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

        @Autowired
        private TaskRepository taskRepository;

        @PostMapping("/")
        public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
            var idUser = request.getAttribute("idUser");
            taskModel.setIdUser((UUID) idUser);

            var currentDate = java.time.LocalDateTime.now();

            // 10/11/2026 - Current
            // 10/10/2026 - Start
            if (currentDate.isAfter(taskModel.getStartAt())){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de início da tarefa não pode ser anterior à data atual.");
            }

            var task = this.taskRepository.save(taskModel);
            return ResponseEntity.status(HttpStatus.OK).body(task);
        }
    }

