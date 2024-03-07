CREATE TABLE public.daily_picks (
    id SERIAL PRIMARY KEY,
    chat_id VARCHAR(255) NOT NULL,
    user_id INTEGER NOT NULL,
    pick_date DATE NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);