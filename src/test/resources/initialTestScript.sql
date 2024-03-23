CREATE TABLE schedule_template
(
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL CHECK(name != ''),
    start_date DATE NOT NULL,
    week_count INTEGER NOT NULL CHECK(week_count > 0),
    is_active BOOLEAN DEFAULT FALSE
);

CREATE TABLE subject
(
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL CHECK(name != '')
);

CREATE TABLE "role"
(
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL CHECK(name != '')
);

CREATE TABLE "group"
(
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL CHECK(name != '')
);

CREATE TABLE attendance_status
(
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL CHECK(name != '')
);

CREATE TABLE "user"
(
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    middle_name VARCHAR(50),
    password VARCHAR(255) NOT NULL,
    login VARCHAR(25) UNIQUE NOT NULL,
    email VARCHAR(50) UNIQUE NOT NULL,
    group_id INTEGER REFERENCES "group" (id) ON DELETE SET NULL,
    role_id INTEGER REFERENCES role (id) NOT NULL
);

CREATE TABLE class
(
    id SERIAL PRIMARY KEY,
    week_number INTEGER NOT NULL,
    user_id INTEGER REFERENCES "user" (id),
    day_of_week INTEGER NOT NULL,
    class_number INTEGER NOT NULL CHECK(class_number > 0),
    audience INTEGER,
    subject_id INTEGER REFERENCES subject (id) ON DELETE CASCADE NOT NULL,
    schedule_template_id INTEGER REFERENCES schedule_template (id) ON DELETE CASCADE NOT NULL
);

CREATE TABLE class_group
(
    id SERIAL PRIMARY KEY,
    group_id INTEGER REFERENCES "group" (id) ON DELETE CASCADE NOT NULL,
    class_id INTEGER REFERENCES class (id) ON DELETE CASCADE NOT NULL
);

CREATE TABLE attendance
(
    id SERIAL PRIMARY KEY,
    attendance_status_id INTEGER REFERENCES attendance_status (id) ON DELETE SET NULL,
    user_id INTEGER REFERENCES "user" (id) ON DELETE CASCADE NOT NULL,
    class_id INTEGER REFERENCES class (id) ON DELETE CASCADE NOT NULL
);

INSERT INTO schedule_template(name, start_date, week_count, is_active) VALUES
    ('first_template', '2024-03-18', 16, TRUE),
    ('second_template', '2024-03-18', 12, FALSE),
    ('third_template', '2024-03-18', 8, FALSE),
    ('fourth_template', '2024-03-18', 14, FALSE);

INSERT INTO subject(name) VALUES
    ('Математические модели и методы безопасного функционирования компонент программного обеспечения'),
    ('Методы обеспечения целостности информации'),
    ('Методы и средства взаимодействия компонент программного обеспечения'),
    ('Модели и методы принятия технических решений');

INSERT INTO "role"(name) VALUES
    ('admin'),
    ('student'),
    ('teacher');

INSERT INTO "group"(name) VALUES
    ('БСБО-01-21'),
    ('БСБО-02-21'),
    ('БСБО-04-21');

INSERT INTO attendance_status(name) VALUES
    ('присутствует'),
    ('отсутствует'),
    ('отсутствует по уважительной причине');

INSERT INTO "user"(first_name, last_name, middle_name, password, login, email, group_id, role_id) VALUES
    ('Тихонова', 'Арина', 'Марковна', '$2a$10$3BuW8KFLzOksrT73XsDD0.4nDvswn0jpm.d9VDvFov0hiOyMGlxW2', 'tih_ar', 'tih_ar@gmail.com', null, 1),
    ('Крылов', 'Степан', 'Адамович', '$2a$10$3BuW8KFLzOksrT73XsDD0.4nDvswn0jpm.d9VDvFov0hiOyMGlxW2', 'kril_step', 'kril_step@gmail.com', null, 3),
    ('Зайцев', 'Лука', 'Ярославович', '$2a$10$3BuW8KFLzOksrT73XsDD0.4nDvswn0jpm.d9VDvFov0hiOyMGlxW2', 'zaiz_luk', 'zaiz_luk@gmail.com', null, 3),
    ('Копылова', 'Маргарита', 'Гордеевна', '$2a$10$3BuW8KFLzOksrT73XsDD0.4nDvswn0jpm.d9VDvFov0hiOyMGlxW2', 'kopil_marg', 'kopil_marg@gmail.com', 1, 2),
    ('Ракова', 'Таисия', 'Ивановна', '$2a$10$3BuW8KFLzOksrT73XsDD0.4nDvswn0jpm.d9VDvFov0hiOyMGlxW2', 'rak_tai', 'rak_tai@gmail.com', 1, 2),
    ('Колесникова', 'Варвара', 'Игоревна', '$2a$10$3BuW8KFLzOksrT73XsDD0.4nDvswn0jpm.d9VDvFov0hiOyMGlxW2', 'kol_var', 'kol_var@gmail.com', 2, 2),
    ('Голубева', 'Кира', 'Дмитриевна', '$2a$10$3BuW8KFLzOksrT73XsDD0.4nDvswn0jpm.d9VDvFov0hiOyMGlxW2', 'gol_kir', 'gol_kir@gmail.com', 2, 2),
    ('Медведева', 'Арина', 'Вячеславовна', '$2a$10$3BuW8KFLzOksrT73XsDD0.4nDvswn0jpm.d9VDvFov0hiOyMGlxW2', 'med_ar', 'med_ar@gmail.com', 3, 2),
    ('Матвеева', 'Елена', 'Фёдоровна', '$2a$10$3BuW8KFLzOksrT73XsDD0.4nDvswn0jpm.d9VDvFov0hiOyMGlxW2', 'mat_el', 'mat_el@gmail.com', 3, 2);

INSERT INTO class (week_number, user_id, day_of_week, class_number, audience, subject_id, schedule_template_id) VALUES
    (1, 2, 1, 1, 255, 1, 1),
    (1, 2, 1, 2, 255, 1, 1),
    (1, 3, 1, 3, 357, 2, 1),
    (1, 3, 1, 4, 357, 2, 1),
    (1, 2, 3, 5, 249, 3, 1),
    (1, 2, 3, 6, 249, 3, 1);

INSERT INTO class_group (group_id, class_id) VALUES
    (1, 1),
    (2, 1),
    (1, 2),
    (2, 2),
    (3, 3),
    (3, 4),
    (2, 5),
    (2, 6);

INSERT INTO attendance(attendance_status_id, user_id, class_id) VALUES
    (1, 4, 1),
    (2, 5, 1),
    (3, 6, 1),
    (1, 7, 1),
    (2, 4, 2),
    (1, 5, 2),
    (1, 6, 2),
    (1, 7, 2),
    (1, 8, 3),
    (3, 9, 3),
    (2, 8, 4),
    (1, 9, 4),
    (null, 6, 5),
    (null, 7, 5),
    (null, 6, 6),
    (null, 7, 6);
