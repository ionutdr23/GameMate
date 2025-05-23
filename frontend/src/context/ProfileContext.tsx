import { createContext } from "react";
import type { Profile } from "@/types/profile";

export type ProfileContextType = {
  profile: Profile | null;
  isLoading: boolean;
  error: string | null;
  refetch: () => void;
  uploadAvatar: (file: File) => Promise<string>;
  updateProfile: (data: Partial<Profile>) => Promise<void>;
};

export const ProfileContext = createContext<ProfileContextType | undefined>(
  undefined
);
