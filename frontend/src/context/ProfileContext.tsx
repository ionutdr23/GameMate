import { createContext } from "react";
import type { Profile } from "@/types/profile";
import { ProfileFormValues } from "@/validation/profile";

export type ProfileContextType = {
  profile: Profile | null;
  isLoading: boolean;
  error: string | null;
  refetch: () => void;
  uploadAvatar: (file: File) => Promise<void>;
  createProfile: (data: ProfileFormValues, avatarFile?: File) => Promise<void>;
  updateProfile: (data: ProfileFormValues) => Promise<void>;
  checkNicknameAvailability: (nickname: string) => Promise<boolean>;
};

export const ProfileContext = createContext<ProfileContextType | undefined>(
  undefined
);
