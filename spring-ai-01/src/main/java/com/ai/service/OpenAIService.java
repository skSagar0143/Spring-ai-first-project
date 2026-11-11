
package com.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OpenAIService {

	private final ChatClient.Builder chatClientBuilder;
	public String askToAi(String questions) {
		return chatClientBuilder.build().prompt().user(questions).call().content();
	}
}
