package org.brainstorm;

import org.brainstorm.model.Comments;
import org.brainstorm.model.Ideas;
import org.brainstorm.model.Rooms;
import org.brainstorm.model.Users;
import org.brainstorm.service.CommentService;
import org.brainstorm.service.IdeaService;
import org.brainstorm.service.RoomService;
import org.brainstorm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration
public class Utils {

    @Autowired
    private RoomService roomService;
    @Autowired
    private UserService userService;
    @Autowired
    private IdeaService ideaService;
    @Autowired
    private CommentService commentService;

    // Métodos helper para eliminar duplicación
    public Rooms createTestRoom(String title, String createdBy, int maxUsers) {
        Rooms room = Rooms.builder()
                .title(title)
                .createdBy(createdBy)
                .maxUsers(maxUsers)
                .build();
        return roomService.save(room);
    }

    public Users createTestUser(String username, Rooms room) {
        Users user = Users.builder()
                .username(username)
                .isOnline(true)
                .room(room)
                .build();
        return userService.create(user);
    }

    public Ideas createTestIdea(String title, String description, Long userId, Long roomId) {
        Ideas idea = Ideas.builder()
                .title(title)
                .description(description)
                .build();
        return ideaService.createIdea(idea, userId, roomId);
    }

    public Comments createTestComment(String content, Long ideaId, Long userId) {
        Comments comment = Comments.builder()
                .content(content)
                .build();
        return commentService.createCommentOnIdea(ideaId, userId, comment);
    }
    public void remove(Long userID){
        roomService.removeUserFromRoom(userID);
    }

    // Configuración común para sala con usuarios
    public TestRoomSetup setupRoomWithUsers() {
        Rooms room = createTestRoom("Test Room", "admin", 30);
        Users user1 = createTestUser("Cesar", room);
        Users user2 = createTestUser("Marta", room);

        room.getUsers().add(user1);
        room.getUsers().add(user2);
        roomService.save(room);

        return new TestRoomSetup(room, user1, user2);
    }

    // Clase interna para organizar datos de setup

    public static class TestRoomSetup {
        public final Rooms room;
        public final Users user1;
        public final Users user2;

        public TestRoomSetup(Rooms room, Users user1, Users user2) {
            this.room = room;
            this.user1 = user1;
            this.user2 = user2;
        }
    }

}
