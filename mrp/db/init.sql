CREATE TABLE users (
        id SERIAL PRIMARY KEY,
        username VARCHAR(50) UNIQUE NOT NULL,
        password_hash VARCHAR(255) NOT NULL,
        email VARCHAR(100),
        favorite_genre VARCHAR(50),
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE media (
        id SERIAL PRIMARY KEY,
        title VARCHAR(200) NOT NULL,
        description TEXT,
        media_type VARCHAR(20) NOT NULL CHECK (media_type IN ('movie','series','game')),
        release_year INT,
        age_restriction INT,
        creator_id INT NOT NULL,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (creator_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE media_genres (
        media_id INT NOT NULL,
        genre VARCHAR(50) NOT NULL,
        PRIMARY KEY (media_id, genre),
        FOREIGN KEY (media_id) REFERENCES media(id) ON DELETE CASCADE
);

CREATE TABLE ratings (
        id SERIAL PRIMARY KEY,
        media_id INT NOT NULL,
        user_id INT NOT NULL,
        stars INT NOT NULL CHECK (stars BETWEEN 1 AND 5),
        comment TEXT,
        confirmed BOOLEAN DEFAULT FALSE,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (media_id) REFERENCES media(id) ON DELETE CASCADE,
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE rating_likes (
        rating_id INT NOT NULL,
        user_id INT NOT NULL,
        PRIMARY KEY (rating_id, user_id),
        FOREIGN KEY (rating_id) REFERENCES ratings(id) ON DELETE CASCADE,
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE favorites (
        user_id INT NOT NULL,
        media_id INT NOT NULL,
        added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        PRIMARY KEY (user_id, media_id),
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
        FOREIGN KEY (media_id) REFERENCES media(id) ON DELETE CASCADE
);
