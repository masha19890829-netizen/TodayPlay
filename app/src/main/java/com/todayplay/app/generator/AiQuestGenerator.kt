package com.todayplay.app.generator

import com.todayplay.app.model.Quest
import com.todayplay.app.model.QuestInput

class AiQuestGenerator : QuestGenerator {
    override fun generate(input: QuestInput): Quest {
        // TODO: Connect OpenAI, Gemini, or a private API here. Do not store API keys in code.
        throw NotImplementedError("AI generation is reserved for a future version.")
    }
}
