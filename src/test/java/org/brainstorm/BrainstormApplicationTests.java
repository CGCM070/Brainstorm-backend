package org.brainstorm;

import org.brainstorm.model.Comments;
import org.brainstorm.model.Ideas;
import org.brainstorm.model.Rooms;
import org.brainstorm.model.Users;
import org.brainstorm.service.CommentService;
import org.brainstorm.service.IdeaService;
import org.brainstorm.service.RoomService;
import org.brainstorm.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.profiles.active=test")
class BrainstormApplicationTests {

    @Autowired
    private PlatformTransactionManager transactionManager;
    private TransactionTemplate transactionTemplate;

    @BeforeEach
    public void setUp() {
        transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Autowired
    private UserService userService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private IdeaService ideaService;

    @Autowired
    private CommentService commentService;

    @Test
    void creandoSala_ConUsuarios() {

        Rooms room = Rooms.builder()
                .createdBy("admin")
                .maxUsers(30)
                .build();
        roomService.save(room);

        Users user1 = Users.builder()
                .username("Cesar")
                .isOnline(true)
                .room(room)
                .build();
        userService.create(user1);

        Users user2 = Users.builder()
                .username("Marta")
                .isOnline(true)
                .room(room)
                .build();
        userService.create(user2);


        // Añadir los usuarios a la sala
        room.getUsers().add(user1);
        room.getUsers().add(user2);

        // Guardar la sala (esto también guardará los usuarios debido a CascadeType.ALL)
        roomService.save(room);
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            // Verificar que los usuarios están en la sala
            assertTrue(room.getUsers().contains(user1));
            assertTrue(room.getUsers().contains(user2));


        });

    }

    @Test
    void RoomWhitUserAndIdea() {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            Rooms room = Rooms.builder()
                    .createdBy("admin")
                    .maxUsers(30)
                    .build();
            roomService.save(room);

            Users user1 = Users.builder()
                    .username("Cesar")
                    .isOnline(true)
                    .room(room)
                    .build();
            userService.create(user1);

            Users user2 = Users.builder()
                    .username("Marta")
                    .isOnline(true)
                    .room(room)
                    .build();
            userService.create(user2);


            // Añadir los usuarios a la sala
            room.getUsers().add(user1);
            room.getUsers().add(user2);

            // Guardar la sala (esto también guardará los usuarios debido a CascadeType.ALL)
            roomService.save(room);

            // Verificar que los usuarios están en la sala
            assertTrue(room.getUsers().contains(user1));
            assertTrue(room.getUsers().contains(user2));


            Ideas idea1 = Ideas.builder()
                    .title("first idea")
                    .description("make more unit test")
                    .totalVotes(1)
                    .build();
            ideaService.createIdea(idea1, user1.getUsername(), room.getId());

            Ideas idea2 = Ideas.builder()
                    .title("second idea")
                    .description("make even more unit test")
                    .totalVotes(6)
                    .build();
            ideaService.createIdea(idea2, user2.getUsername(), room.getId());

            assertNotNull(room.getIdeas());
            assertTrue(room.getIdeas().stream().anyMatch(ideas -> ideas.getTitle().equals("second idea")));

        });
    }

    @Test
    void RoomWhitUserAndIdea_AndComments() {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            Rooms room = Rooms.builder()
                    .createdBy("admin")
                    .maxUsers(30)
                    .build();
            roomService.save(room);

            Users user1 = Users.builder()
                    .username("Cesar")
                    .isOnline(true)
                    .room(room)
                    .build();
            userService.create(user1);

            Users user2 = Users.builder()
                    .username("Marta")
                    .isOnline(true)
                    .room(room)
                    .build();
            userService.create(user2);


            // Añadir los usuarios a la sala
            room.getUsers().add(user1);
            room.getUsers().add(user2);

            // Guardar la sala (esto también guardará los usuarios debido a CascadeType.ALL)
            roomService.save(room);

            // Verificar que los usuarios están en la sala
            assertTrue(room.getUsers().contains(user1));
            assertTrue(room.getUsers().contains(user2));

            Ideas idea2 = Ideas.builder()
                    .title("second idea")
                    .description("make even more unit test")
                    .totalVotes(6)
                    .build();
            ideaService.createIdea(idea2, user2.getUsername(), room.getId());

            Ideas idea1 = Ideas.builder()
                    .title("first idea")
                    .description("make more unit test")
                    .totalVotes(1)
                    .build();
            ideaService.createIdea(idea1, user1.getUsername(), room.getId());

            assertNotNull(room.getIdeas());
            assertTrue(room.getIdeas().stream().anyMatch(ideas -> ideas.getTitle().equals("second idea")));

            Comments comentario1 = Comments.builder()
                    .content("first comment ")
                    .build();

            Comments saved = commentService.createCommentOnIdea(idea1.getId(), user1.getId(), comentario1);
            assertNotNull(saved.getId());

            //assertTrue(idea1.getComments().contains(saved)); Falla

            // Usamos stream().anyMatch(...) en lugar de contains()
            // porque al usar un Set<Comments> con equals/hashCode basados en id,
            // el hashCode cambia tras persistir (id deja de ser null) y contains() falla.
            assertTrue(
                    idea1.getComments()
                            .stream()
                            .anyMatch(c -> c.getId().equals(saved.getId()))
            );

        });
    }

    @Test
    void createUserAndOwnRoom_othersJoin() {

        transactionTemplate.executeWithoutResult(transactionStatus -> {
            Users user = Users.builder()
                    .username("Tachanka")
                    .isOnline(true)
                    .build();

            Users created = userService.create(user);

            Rooms room = Rooms.builder()
                    .title("R6X Operators")
                    .maxUsers(30)
                    .build();
            Rooms createdRoom = roomService.save(room);
            roomService.createRoomWithUser(created.getId(), createdRoom);

            assertTrue(createdRoom.getCreatedBy().equalsIgnoreCase(user.getUsername()));
            assertTrue(createdRoom.getUsers().stream()
                    .anyMatch(users -> users.getId().equals(created.getId())));


            Users user2 = Users.builder()
                    .username("Frost")
                    .isOnline(true)
                    .build();

            Users friendJoin = userService.create(user2);
            roomService.joinRoom(createdRoom.getCode(), friendJoin.getId());

            assertTrue(createdRoom.getUsers().stream()
                    .anyMatch(users -> users.getUsername().equalsIgnoreCase(friendJoin.getUsername())));

            assertTrue(friendJoin.getRoom().getCode().equalsIgnoreCase(createdRoom.getCode()));


            Ideas friendIdea = Ideas.builder()
                    .title("FrostIdea here")
                    .description(" We need to use C4")
                    .build();

            Ideas savedIdea = ideaService.
                    createIdea(friendIdea,
                    user2.getUsername(),
                    createdRoom.getId());

            //aprovao por tachanka y comentado
            ideaService.votarIdea(savedIdea.getId(), created.getUsername(), 1);
            Comments tachankaComments = Comments.builder()
                    .content("yess sr")
                    .build();

            commentService.createCommentOnIdea(
                    savedIdea.getId(),
                    created.getId(),
                    tachankaComments);

            assertFalse(createdRoom.getIdeas().isEmpty());
            assertEquals( savedIdea.getAuthor(),friendJoin.getUsername());
            assertEquals( 1,savedIdea.getTotalVotes());
        });
    }
}
