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
  name: string;
  icon: string;
  skill: string;
  playstyles: string[];
  platforms: string[];
};
