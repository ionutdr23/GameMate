import { Game } from "./game";

export type Profile = {
  id: string;
  nickname: string;
  avatarUrl: string;
  bio?: string;
  location?: string;
  createdAt: Date;
  gameProfiles?: Array<GameProfile>;
  friends: Array<Profile>;
  sentFriendRequests: Array<FriendRequest>;
  receivedFriendRequests: Array<FriendRequest>;
};

export type ProfilePreview = {
  id: string;
  nickname: string;
  avatarUrl: string;
};

export type GameProfile = {
  id: string;
  game: Game;
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

type FriendRequest = {
  id: string;
  createdAt: string;
  sender: Profile;
  receiver: Profile;
};
