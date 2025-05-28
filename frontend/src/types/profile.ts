export type Profile = {
  id: string;
  nickname: string;
  avatarUrl: string;
  bio?: string;
  location?: string;
  createdAt: Date;
  gameProfiles?: Array<GameProfile>;
};

export type GameProfile = {
  id: string;
  game: {
    id: string;
    name: string;
    skillLevels: string[];
  };
  skillLevel: string;
  playstyles: string[];
  platforms: string[];
};

export type GameProfileRequest = {
  gameId: string;
  skillLevel: string;
  playstyles: string[];
  platforms: string[];
};

export type SearchProfileResponse = {
  profileId: string;
  nickname: string;
  avatarUrl?: string;
  isFriend: boolean;
};
