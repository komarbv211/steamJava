-- Створення бази даних (якщо потрібно)
CREATE DATABASE steam;

-- Використання бази даних
\c steam;

-- Таблиця користувачів
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Таблиця ігор
CREATE TABLE games (
    id SERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    developer VARCHAR(100) NOT NULL,
    publisher VARCHAR(100) NOT NULL,
    release_date DATE NOT NULL,
    price DECIMAL(10, 2) NOT NULL CHECK (price >= 0)
);

-- Таблиця покупок
CREATE TABLE purchases (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(id) ON DELETE CASCADE,
    game_id INT REFERENCES games(id) ON DELETE CASCADE,
    purchase_date TIMESTAMP DEFAULT NOW(),
    price DECIMAL(10,2) NOT NULL CHECK (price >= 0),
    UNIQUE(user_id, game_id) -- Користувач не може купити одну гру двічі
);

-- Таблиця відгуків
CREATE TABLE reviews (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(id) ON DELETE CASCADE,
    game_id INT REFERENCES games(id) ON DELETE CASCADE,
    rating INT CHECK (rating BETWEEN 1 AND 10),
    review_text TEXT,
    created_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(user_id, game_id) -- Один відгук на гру від користувача
);

-- Таблиця друзів
CREATE TABLE friends (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(id) ON DELETE CASCADE,
    friend_id INT REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT NOW(),
    CHECK (user_id <> friend_id), -- Користувач не може бути другом самому собі
    UNIQUE(user_id, friend_id)  -- Уникнення дублювань
);