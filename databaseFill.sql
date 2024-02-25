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
    ('Тихонова', 'Арина', 'Марковна', '1234', 'tih_ar', 'tih_ar@gmail.com', null, 1),
    ('Крылов', 'Степан', 'Адамович', '1234', 'kril_step', 'kril_step@gmail.com', null, 3),
    ('Зайцев', 'Лука', 'Ярославович', '1234', 'zaiz_luk', 'zaiz_luk@gmail.com', null, 3),
    ('Копылова', 'Маргарита', 'Гордеевна', '1234', 'kopil_marg', 'kopil_marg@gmail.com', 1, 2),
    ('Ракова', 'Таисия', 'Ивановна', '1234', 'rak_tai', 'rak_tai@gmail.com', 1, 2),
    ('Колесникова', 'Варвара', 'Игоревна', '1234', 'kol_var', 'kol_var@gmail.com', 2, 2),
    ('Голубева', 'Кира', 'Дмитриевна', '1234', 'gol_kir', 'gol_kir@gmail.com', 2, 2),
    ('Медведева', 'Арина', 'Вячеславовна', '1234', 'med_ar', 'med_ar@gmail.com', 3, 2),
    ('Матвеева', 'Елена', 'Фёдоровна', '1234', 'mat_el', 'mat_el@gmail.com', 3, 2);

INSERT INTO education_day(week_number, user_id, "date", class_number, audience, subject_id) VALUES
    (1, 2, '2024-02-19', 1, 255, 1),
    (1, 2, '2024-02-19', 2, 255, 1),
    (1, 3, '2024-02-19', 3, 357, 2),
    (1, 3, '2024-02-19', 4, 357, 2),
    (1, 2, '2024-02-21', 5, 249, 3),
    (1, 2, '2024-02-21', 6, 249, 3);

INSERT INTO education_day_group(group_id, education_day_id) VALUES
    (1, 1),
    (2, 1),
    (1, 2),
    (2, 2),
    (3, 3),
    (3, 4),
    (2, 5),
    (2, 6);

INSERT INTO attendance(attendance_status_id, user_id, education_day_id) VALUES
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
