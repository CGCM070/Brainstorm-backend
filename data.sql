use brainstorm_db;

-- Insertar ROOMS
INSERT INTO rooms (title, code, created_by, created_at, max_users) VALUES
                                                                       ('Sala Principal', 'ABC123', 'admin', NOW(), 10),
                                                                       ('Sala de Gestión', 'XYZ789', 'manager', NOW(), 15),
                                                                       ('Sala de Usuarios', 'DEF456', 'user1', NOW(), 8);

-- Insertar USERS
INSERT INTO users (username, is_online, room_id) VALUES
                                                     ('alice', 1, 1),
                                                     ('bob', 1, 1),
                                                     ('charlie', 0, 1),
                                                     ('diana', 1, 2),
                                                     ('eve', 1, 2),
                                                     ('frank', 0, 3);

-- Insertar IDEAS (agregando user_id)
INSERT INTO ideas (title, description, author, total_votes, created_at, updated_at, room_id, user_id) VALUES
                                                                                                          ('Mejorar la UI', 'Rediseñar la interfaz para hacerla más intuitiva', 'alice', 5, NOW(), NOW(), 1, 1),
                                                                                                          ('Sistema de notificaciones', 'Implementar notificaciones push en tiempo real', 'bob', 3, NOW(), NOW(), 1, 2),
                                                                                                          ('App móvil', 'Desarrollar una aplicación móvil nativa', 'diana', 8, NOW(), NOW(), 2, 4),
                                                                                                          ('Chat integrado', 'Añadir funcionalidad de chat dentro de las salas', 'eve', 2, NOW(), NOW(), 2, 5),
                                                                                                          ('Modo oscuro', 'Implementar tema oscuro para la aplicación', 'frank', 1, NOW(), NOW(), 3, 6);

INSERT INTO comments (author_username, content, created_at, updated_at, idea_id) VALUES
                                                                                     ('bob',     'Excelente idea, me parece muy necesario',  NOW(), NOW(), 1),
                                                                                     ('charlie','Podríamos usar Material Design',           NOW(), NOW(), 1),
                                                                                     ('alice',  '¿Qué tecnología usaríamos?',               NOW(), NOW(), 2),
                                                                                     ('diana',  'Firebase sería una buena opción',          NOW(), NOW(), 2),
                                                                                     ('eve',    'React Native o Flutter?',                  NOW(), NOW(), 3),
                                                                                     ('alice',  'Me gusta más Flutter',                     NOW(), NOW(), 3),
                                                                                     ('frank',  'Socket.io funcionaría bien para esto',     NOW(), NOW(), 4),
                                                                                     ('diana',  'También podríamos usar WebRTC',            NOW(), NOW(), 4),
                                                                                     ('alice',  'Muy buena idea para la vista nocturna',    NOW(), NOW(), 5);