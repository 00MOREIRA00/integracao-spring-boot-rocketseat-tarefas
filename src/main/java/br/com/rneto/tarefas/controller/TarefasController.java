package br.com.rneto.tarefas.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tarefas")
public class TarefasController {

    @GetMapping("/health")
    public String healthCheack() {
        return "OK";
    }

}