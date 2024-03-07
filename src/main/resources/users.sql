CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    chat_id VARCHAR(255) NOT NULL,
    username VARCHAR(255),
    beauty_count INTEGER DEFAULT 0
);