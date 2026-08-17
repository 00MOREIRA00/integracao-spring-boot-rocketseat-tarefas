package br.com.rneto.tarefas.task;

import br.com.rneto.tarefas.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import jdk.jshell.execution.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
            if (currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de início e/ou de termino da tarefa não pode ser anterior à data atual.");
            }

            if (taskModel.getStartAt().isAfter(taskModel.getEndAt())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de início não pode ser posterior à data de término da tarefa.");
            }

            var task = this.taskRepository.save(taskModel);
            return ResponseEntity.status(HttpStatus.OK).body(task);
        }

        @GetMapping("/")
        public List<TaskModel> list(HttpServletRequest request) {
            var idUser = request.getAttribute("idUser");
            var tasks = this.taskRepository.findByIdUser((UUID) idUser);
            return tasks;
        }

        @PutMapping("/{id}")
        public ResponseEntity update(@RequestBody TaskModel taskModel, @PathVariable UUID id, HttpServletRequest request) {
            var idUser = request.getAttribute("idUser");

            var task = this.taskRepository.findById(id).orElse(null);

            if (task == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tarefa não encontrada");
            }

            if (!taskModel.getIdUser().equals(idUser)){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário não tem permissão de altyerar essa task");
            }



            Utils.copyNonNullProperties(taskModel, task);

            var taskUpdated = this.taskRepository.save(task);

            return ResponseEntity.ok().body(taskUpdated);
        }
    }

