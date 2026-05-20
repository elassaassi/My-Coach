package org.elas.momentum.messaging.domain;

import org.elas.momentum.messaging.domain.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.*;

class MessagingDomainTest {

    // ── ConversationId ────────────────────────────────────────────────────────

    @Test
    @DisplayName("ConversationId.generate() produit des IDs uniques non nuls")
    void conversationId_generate_isUnique() {
        var a = ConversationId.generate();
        var b = ConversationId.generate();
        assertThat(a).isNotNull();
        assertThat(a.value()).isNotBlank();
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    @DisplayName("ConversationId.of() restitue la valeur")
    void conversationId_of_restoresValue() {
        var id = ConversationId.of("conv-123");
        assertThat(id.value()).isEqualTo("conv-123");
    }

    // ── MessageId ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("MessageId.generate() produit des IDs uniques non nuls")
    void messageId_generate_isUnique() {
        var a = MessageId.generate();
        var b = MessageId.generate();
        assertThat(a).isNotNull();
        assertThat(a.value()).isNotBlank();
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    @DisplayName("MessageId.of() restitue la valeur")
    void messageId_of_restoresValue() {
        var id = MessageId.of("msg-42");
        assertThat(id.value()).isEqualTo("msg-42");
    }

    // ── Message ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Message.create() nominal — statut SENT, contenu préservé")
    void message_create_nominal() {
        var convId = ConversationId.generate();
        var msg = Message.create(convId, "user-1", "Bonjour !");
        assertThat(msg.getId()).isNotNull();
        assertThat(msg.getContent()).isEqualTo("Bonjour !");
        assertThat(msg.getSenderId()).isEqualTo("user-1");
        assertThat(msg.getStatus()).isEqualTo(MessageStatus.SENT);
        assertThat(msg.getSentAt()).isNotNull();
    }

    @Test
    @DisplayName("Message.create() contenu vide lève IllegalArgumentException")
    void message_create_blankContent_throws() {
        var convId = ConversationId.generate();
        assertThatThrownBy(() -> Message.create(convId, "user-1", ""))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Message.create(convId, "user-1", "   "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Message.create() contenu null lève IllegalArgumentException")
    void message_create_nullContent_throws() {
        var convId = ConversationId.generate();
        assertThatThrownBy(() -> Message.create(convId, "user-1", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Message.create() contenu > 2000 chars lève IllegalArgumentException")
    void message_create_tooLong_throws() {
        var convId = ConversationId.generate();
        var tooLong = "x".repeat(2001);
        assertThatThrownBy(() -> Message.create(convId, "user-1", tooLong))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Message.create() contenu de exactement 2000 chars est accepté")
    void message_create_exactLimit_accepted() {
        var convId = ConversationId.generate();
        var exactly2000 = "x".repeat(2000);
        var msg = Message.create(convId, "user-1", exactly2000);
        assertThat(msg.getContent()).hasSize(2000);
    }

    @Test
    @DisplayName("Message.markRead() passe le statut à READ")
    void message_markRead() {
        var msg = Message.create(ConversationId.generate(), "u1", "Hello");
        msg.markRead();
        assertThat(msg.getStatus()).isEqualTo(MessageStatus.READ);
    }

    @Test
    @DisplayName("Message.markDelivered() passe SENT → DELIVERED")
    void message_markDelivered_fromSent() {
        var msg = Message.create(ConversationId.generate(), "u1", "Hello");
        msg.markDelivered();
        assertThat(msg.getStatus()).isEqualTo(MessageStatus.DELIVERED);
    }

    @Test
    @DisplayName("Message.markDelivered() sur READ ne change pas le statut")
    void message_markDelivered_fromRead_noChange() {
        var msg = Message.create(ConversationId.generate(), "u1", "Hello");
        msg.markRead();
        msg.markDelivered();
        assertThat(msg.getStatus()).isEqualTo(MessageStatus.READ);
    }

    // ── Conversation ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Conversation.create() nominal — deux participants distincts")
    void conversation_create_nominal() {
        var conv = Conversation.create("user-A", "user-B");
        assertThat(conv.getId()).isNotNull();
        assertThat(conv.getParticipantA()).isEqualTo("user-A");
        assertThat(conv.getParticipantB()).isEqualTo("user-B");
        assertThat(conv.getLastMessagePreview()).isNull();
        assertThat(conv.getLastMessageAt()).isNull();
        assertThat(conv.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Conversation.create() avec le même utilisateur lève IllegalArgumentException")
    void conversation_create_sameUser_throws() {
        assertThatThrownBy(() -> Conversation.create("user-A", "user-A"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Conversation.create() avec null lève NullPointerException")
    void conversation_create_null_throws() {
        assertThatThrownBy(() -> Conversation.create(null, "user-B"))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> Conversation.create("user-A", null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Conversation.sendMessage() met à jour le preview et lastMessageAt")
    void conversation_sendMessage_updatesPreview() {
        var conv = Conversation.create("user-A", "user-B");
        var msg = conv.sendMessage("user-A", "Salut !");
        assertThat(msg).isNotNull();
        assertThat(conv.getLastMessagePreview()).isEqualTo("Salut !");
        assertThat(conv.getLastMessageAt()).isNotNull();
    }

    @Test
    @DisplayName("Conversation.sendMessage() tronque le preview à 50 chars + ellipse")
    void conversation_sendMessage_truncatesLongPreview() {
        var conv = Conversation.create("user-A", "user-B");
        var longContent = "A".repeat(60);
        conv.sendMessage("user-B", longContent);
        assertThat(conv.getLastMessagePreview()).hasSize(51).endsWith("…");
    }

    @Test
    @DisplayName("Conversation.sendMessage() par un non-participant lève IllegalArgumentException")
    void conversation_sendMessage_nonParticipant_throws() {
        var conv = Conversation.create("user-A", "user-B");
        assertThatThrownBy(() -> conv.sendMessage("user-C", "Hello"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not a participant");
    }

    @Test
    @DisplayName("Conversation.isParticipant() retourne vrai pour A et B, faux pour un tiers")
    void conversation_isParticipant() {
        var conv = Conversation.create("user-A", "user-B");
        assertThat(conv.isParticipant("user-A")).isTrue();
        assertThat(conv.isParticipant("user-B")).isTrue();
        assertThat(conv.isParticipant("user-C")).isFalse();
    }

    @Test
    @DisplayName("Conversation.getOtherParticipant() retourne l'autre selon qui appelle")
    void conversation_getOtherParticipant() {
        var conv = Conversation.create("user-A", "user-B");
        assertThat(conv.getOtherParticipant("user-A")).isEqualTo("user-B");
        assertThat(conv.getOtherParticipant("user-B")).isEqualTo("user-A");
    }

    @Test
    @DisplayName("Conversation.getParticipants() retourne les deux participants")
    void conversation_getParticipants() {
        var conv = Conversation.create("user-A", "user-B");
        assertThat(conv.getParticipants()).containsExactlyInAnyOrder("user-A", "user-B");
    }

    @Test
    @DisplayName("Conversation reconstitution préserve tous les champs")
    void conversation_reconstitute() {
        var id = ConversationId.generate();
        var now = Instant.now();
        var conv = new Conversation(id, "A", "B", "preview", now, now);
        assertThat(conv.getId()).isEqualTo(id);
        assertThat(conv.getLastMessagePreview()).isEqualTo("preview");
        assertThat(conv.getLastMessageAt()).isEqualTo(now);
    }
}