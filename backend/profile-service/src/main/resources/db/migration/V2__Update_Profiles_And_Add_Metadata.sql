CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TABLE IF EXISTS availability_slots, friend_requests, friendships, user_blocks, game_profile_roles, game_profiles, game_roles, game_skill_levels, games, profiles CASCADE;

CREATE TABLE profiles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    auth0_id VARCHAR(100) UNIQUE NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    avatar_url VARCHAR(255),
    bio TEXT,
    country VARCHAR(50),
    city VARCHAR(50),
    profile_visibility VARCHAR(20) DEFAULT 'PUBLIC' CHECK (profile_visibility IN ('PUBLIC', 'FRIENDS', 'PRIVATE')),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE games (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) UNIQUE NOT NULL,
    slug VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE game_roles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    game_id UUID REFERENCES games(id) ON DELETE CASCADE,
    role_name VARCHAR(50) NOT NULL,
    UNIQUE (game_id, role_name)
);

CREATE TABLE game_skill_levels (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    game_id UUID REFERENCES games(id) ON DELETE CASCADE,
    level_name VARCHAR(50) NOT NULL,
    level_order INTEGER NOT NULL,
    UNIQUE (game_id, level_name)
);

CREATE TABLE game_profiles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    profile_id UUID REFERENCES profiles(id) ON DELETE CASCADE,
    game_id UUID REFERENCES games(id) ON DELETE CASCADE,
    skill_level_id UUID REFERENCES game_skill_levels(id),
    playstyle_tags TEXT[],
    platform_tags TEXT[]
);

CREATE TABLE game_profile_roles (
    game_profile_id UUID REFERENCES game_profiles(id) ON DELETE CASCADE,
    game_role_id UUID REFERENCES game_roles(id) ON DELETE CASCADE,
    PRIMARY KEY (game_profile_id, game_role_id)
);

CREATE TABLE availability_slots (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    profile_id UUID REFERENCES profiles(id) ON DELETE CASCADE,
    day_of_week VARCHAR(10) CHECK (day_of_week IN ('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY')),
    start_time TIME NOT NULL,
    end_time TIME NOT NULL
);

CREATE TABLE friendships (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_one_id UUID NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
    user_two_id UUID NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
    UNIQUE (user_one_id, user_two_id),
    CHECK (user_one_id <> user_two_id)
);

CREATE TABLE friend_requests (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    sender_id UUID NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
    receiver_id UUID NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
    status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED', 'CANCELLED')),
    sent_at TIMESTAMP DEFAULT NOW() NOT NULL,
    responded_at TIMESTAMP,
    UNIQUE (sender_id, receiver_id),
    CHECK (sender_id <> receiver_id)
);

CREATE TABLE user_blocks (
    blocker_id UUID NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
    blocked_id UUID NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
    blocked_at TIMESTAMP DEFAULT NOW() NOT NULL ,
    PRIMARY KEY (blocker_id, blocked_id),
    CHECK (blocker_id <> blocked_id)
);