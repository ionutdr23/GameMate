export type Profile = {
  nickname: string;
  avatarUrl: string;
  bio?: string;
  location?: string;
  createdAt: Date;
  availability?: Array<AvailabilitySlot>;
  gameProfiles?: Array<GameProfile>;
};

export type AvailabilitySlot = { day: string; from: string; to: string };

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
