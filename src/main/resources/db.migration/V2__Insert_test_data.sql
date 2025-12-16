-- Вставляем тестовых пользователей
INSERT INTO users (name, email, password) VALUES
('John Doe', 'john.doe@example.com', '$2a$10$ExampleHash1'), -- Пароль: password123
('Jane Smith', 'jane.smith@example.com', '$2a$10$ExampleHash2'); -- Пароль: password456

-- Вставляем тестовые задачи
INSERT INTO tasks (title, description, due_date, status, creator_id) VALUES
('Task 1', 'Description for Task 1', '2023-12-01', 'IN_PROGRESS', 1),
('Task 2', 'Description for Task 2', '2023-12-15', 'TODO', 2);

-- Вставляем тестовые теги
INSERT INTO tags (name) VALUES
('Urgent'),
('Important'),
('Low Priority');

-- Вставляем связи между задачами и тегами
INSERT INTO task_tags (task_id, tag_id) VALUES
(1, 1), -- Task 1 -> Urgent
(1, 2), -- Task 1 -> Important
(2, 3); -- Task 2 -> Low Priority