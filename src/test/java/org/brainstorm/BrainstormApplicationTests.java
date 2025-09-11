package org.brainstorm;

import org.brainstorm.model.Ideas;
import org.brainstorm.service.VoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.profiles.active=test")
@Import(Utils.class)
class BrainstormApplicationTests {

    @Autowired
    private PlatformTransactionManager transactionManager;
    private TransactionTemplate transactionTemplate;

    @Autowired
    private Utils utils;

    @Autowired
    private VoteService voteService;

    @BeforeEach
    public void setUp() {
        transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Test
    void creandoSala_ConUsuarios() {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            // Given & When -  helper para crear sala con usuarios
            Utils.TestRoomSetup setup = utils.setupRoomWithUsers();

            // Then - Verificar que los usuarios están en la sala
            assertTrue(setup.room.getUsers().contains(setup.user1));
            assertTrue(setup.room.getUsers().contains(setup.user2));
        });
    }

    @Test
    void RoomWhitUserAndIdea() {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            // Given - Usar helper para setup inicial
            Utils.TestRoomSetup setup = utils.setupRoomWithUsers();

            // When - Crear ideas usando helper
            Ideas idea1 = utils.createTestIdea("first idea", "make more unit test", setup.user1.getId(), setup.room.getId());
            Ideas idea2 = utils.createTestIdea("second idea", "make even more unit test", setup.user2.getId(), setup.room.getId());

            // Then - Verificar que las ideas están en la sala
            assertNotNull(setup.room.getIdeas());
            assertTrue(setup.room.getIdeas().stream().anyMatch(ideas -> ideas.getTitle().equals("second idea")));
        });
    }

    @Test
    void RoomWhitUserAndIdea_AndComments() {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            // Given - Setup inicial con helper
            Utils.TestRoomSetup setup = utils.setupRoomWithUsers();

            // When - Crear ideas
            Ideas idea1 = utils.createTestIdea("first idea", "make more unit test", setup.user1.getId(), setup.room.getId());
            Ideas idea2 = utils.createTestIdea("second idea", "make even more unit test", setup.user2.getId(), setup.room.getId());

            // Then - Verificar ideas
            assertNotNull(setup.room.getIdeas());
            assertTrue(setup.room.getIdeas().stream().anyMatch(ideas -> ideas.getTitle().equals("second idea")));

            // When - Crear comentario usando helper
            utils.createTestComment("first comment", idea1.getId(), setup.user1.getId());

            // Then - Verificar que el comentario se creó
            assertTrue(idea1.getComments().stream().anyMatch(c -> c.getContent().equals("first comment")));
        });
    }

    @Test
    void createUserAndOwnRoom_othersJoin() {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            // Given - Crear usuarios y sala usando helpers
            var room = utils.createTestRoom("R6X Operators", "admin", 30);
            var user1 = utils.createTestUser("Tachanka", room);
            var user2 = utils.createTestUser("Frost", room);

            // Agregar usuarios a la sala manualmente para este test específico
            room.getUsers().add(user1);
            room.getUsers().add(user2);

            // When - Crear idea y votar
            Ideas friendIdea = utils.createTestIdea("FrostIdea here", "We need to use C4", user2.getId(), room.getId());
            Ideas votedIdea = voteService.votarIdea(friendIdea.getId(), user1.getId(), 1);

            // When - Crear comentario
            utils.createTestComment("yess sr", friendIdea.getId(), user1.getId());

            // Then - Verificaciones
            assertFalse(room.getIdeas().isEmpty());
            assertEquals(user2.getUsername(), votedIdea.getAuthor());
            assertEquals(1, votedIdea.getTotalVotes());
        });
    }

    @Test
    void removeUserFromRoom() {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            // Given - Setup completo usando helpers
            Utils.TestRoomSetup setup = utils.setupRoomWithUsers();

            // When - Crear idea, votar y comentar
            Ideas idea = utils.createTestIdea("FrostIdea here", "We need to use C4", setup.user2.getId(), setup.room.getId());
            voteService.votarIdea(idea.getId(), setup.user1.getId(), 1);
            utils.createTestComment("yess sr", idea.getId(), setup.user1.getId());

            // Verificar estado antes de remover
            assertFalse(setup.room.getIdeas().isEmpty());
            assertEquals(setup.user2.getUsername(), idea.getAuthor());
            assertEquals(1, idea.getTotalVotes());

            // When - Remover usuario
            utils.remove(setup.user2.getId());

            // Then - Verificar remoción
            assertFalse(setup.room.getUsers().stream().anyMatch(users -> users.getId().equals(setup.user2.getId())));
            assertNull(setup.user2.getRoom());
        });
    }

    @Test
    void voteService_CrearNuevoVoto() {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            // Given - Setup usando helper
            Utils.TestRoomSetup setup = utils.setupRoomWithUsers();
            Ideas idea = utils.createTestIdea("Test Idea", "Test Description", setup.user2.getId(), setup.room.getId());

            // Verificar estado inicial
            assertEquals(0, idea.getTotalVotes());
            assertEquals(0, idea.getVotes().size());

            // When - Votar usando VoteService
            Ideas votedIdea = voteService.votarIdea(idea.getId(), setup.user1.getId(), 1);

            // Then - Verificar resultado
            assertEquals(1, votedIdea.getTotalVotes());
            assertEquals(1, votedIdea.getVotes().size());
        });
    }
}
