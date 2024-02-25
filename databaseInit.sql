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
    password VARCHAR(30) NOT NULL,
    login VARCHAR(25) UNIQUE NOT NULL,
    email VARCHAR(50) UNIQUE NOT NULL,
    group_id INTEGER REFERENCES "group" (id) ON DELETE SET NULL,
    role_id INTEGER REFERENCES role (id) NOT NULL
);

CREATE TABLE education_day
(
    id SERIAL PRIMARY KEY,
    week_number INTEGER NOT NULL,
    user_id INTEGER REFERENCES "user" (id),
    "date" DATE NOT NULL,
    class_number INTEGER NOT NULL CHECK(class_number > 0),
    audience INTEGER,
    subject_id INTEGER REFERENCES subject (id) ON DELETE CASCADE NOT NULL
);

CREATE TABLE education_day_group
(
    id SERIAL PRIMARY KEY,
    group_id INTEGER REFERENCES "group" (id) NOT NULL,
    education_day_id INTEGER REFERENCES education_day (id) ON DELETE CASCADE NOT NULL
);

CREATE TABLE attendance
(
    id SERIAL PRIMARY KEY,
    attendance_status_id INTEGER REFERENCES attendance_status (id) ON DELETE SET NULL,
    user_id INTEGER REFERENCES "user" (id) ON DELETE CASCADE NOT NULL,
    education_day_id INTEGER REFERENCES education_day (id) ON DELETE CASCADE NOT NULL
);