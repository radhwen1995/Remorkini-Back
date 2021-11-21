package com.onegateafrica.Repositories;

import com.onegateafrica.Entities.ChatConversation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatConversationRepository extends JpaRepository<ChatConversation,Long> {
}
