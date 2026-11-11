
package com.ai.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ai.service.OpenAIService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class OpenAIController {

	private final OpenAIService service;

	@GetMapping("/")
	public String askQuestions() {
		return "chatPage";
	}

	@PostMapping("/sendMsg")
	@ResponseBody
	public String sendAnswer(@RequestParam("question") String question) {
		return service.askToAi(question);
	}
}
