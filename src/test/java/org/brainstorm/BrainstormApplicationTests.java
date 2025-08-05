package org.brainstorm;

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
@SpringBootTest
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
    void createRoomWithIdeasAndComments() {
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


        });

    }
}
