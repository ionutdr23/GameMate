CREATE TABLE profiles (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    avatar_url VARCHAR(255),
    bio TEXT,
    last_login TIMESTAMP,
    created_at TIMESTAMP,
    country VARCHAR(50),
    city VARCHAR(50)
);
