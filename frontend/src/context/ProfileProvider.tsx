import { useCallback, useEffect, useState } from "react";
import { ProfileContext, ProfileContextType } from "./ProfileContext";
import { useAxiosWithAuth } from "@/lib/utils";
import type { Profile } from "@/types/profile";
import axios from "axios";
import { ProfileFormValues } from "@/validation/profile";

export const ProfileProvider = ({
  children,
}: {
  children: React.ReactNode;
}) => {
  const axiosInstance = useAxiosWithAuth();
  const [profile, setProfile] = useState<Profile | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const refetch = useCallback(async () => {
    setIsLoading(true);
    setError(null);
    try {
      const res = await axiosInstance.get("/user/profile/me");
      if (res.status === 404) {
        setProfile(null);
        return;
      }
      const data = res.data;
      const parsed = {
        ...data,
        createdAt: new Date(data.createdAt),
      };
      setProfile(parsed);
    } catch (err) {
      setError((err as Error).message);
      setProfile(null);
    } finally {
      setIsLoading(false);
    }
  }, [axiosInstance]);

  const uploadAvatar = async (file: File): Promise<void> => {
    setIsLoading(true);
    setError(null);
    try {
      const formData = new FormData();
      formData.append("file", file);
      const res = await axiosInstance.post("/user/profile/avatar", formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });
      const data = res.data;
      setProfile((prev) =>
        prev ? { ...prev, avatarUrl: data.avatarUrl as string } : prev
      );
    } catch (err) {
      setError((err as Error).message);
      throw new Error("Upload avatar failed");
    } finally {
      setIsLoading(false);
    }
  };

  const createProfile = async (
    data: ProfileFormValues,
    avatarFile?: File
  ): Promise<void> => {
    setIsLoading(true);
    setError(null);
    try {
      await axiosInstance.post("/user/profile", data, {
        headers: { "Content-Type": "application/json" },
      });
      if (avatarFile) {
        await uploadAvatar(avatarFile);
      }
      await refetch();
    } catch (err) {
      setError((err as Error).message);
      throw new Error("Profile creation failed");
    } finally {
      setIsLoading(false);
    }
  };

  const updateProfile = async (data: ProfileFormValues) => {
    setIsLoading(true);
    setError(null);
    try {
      const res = await axiosInstance.put("/user/profile", data, {
        headers: { "Content-Type": "application/json" },
      });
      const updatedProfile = res.data;
      setProfile({
        ...updatedProfile,
        createdAt: new Date(updatedProfile.createdAt),
      });
    } catch (err) {
      setError((err as Error).message);
      throw new Error("Profile update failed");
    } finally {
      setIsLoading(false);
    }
  };

  const deleteAccount = async (): Promise<void> => {
    setIsLoading(true);
    setError(null);
    try {
      const response = await axiosInstance.delete("/user/profile/delete");
      if (response.status !== 204) {
        throw new Error("Failed to delete account");
      }
    } catch (err) {
      setError((err as Error).message);
      throw new Error("Failed to delete account");
    } finally {
      setIsLoading(false);
    }
  };

  const checkNicknameAvailability = async (
    nickname: string
  ): Promise<boolean> => {
    try {
      const res = await axiosInstance.get("/user/profile/check-nickname", {
        params: { nickname },
      });
      return res.data.available;
    } catch (err) {
      if (
        axios.isAxiosError(err) &&
        err.response?.status === 400 &&
        err.response.data?.available === false
      ) {
        return false;
      }

      setError((err as Error).message);
      throw new Error("Error checking nickname availability");
    }
  };

  useEffect(() => {
    try {
      refetch();
    } catch (err) {
      if ((err as Error).message === "Login required") {
        setError("Login required");
      } else {
        setError((err as Error).message || "An unknown error occurred");
      }
      setProfile(null);
    }
  }, [refetch]);

  const contextValue: ProfileContextType = {
    profile,
    isLoading,
    error,
    refetch,
    uploadAvatar,
    createProfile,
    updateProfile,
    deleteAccount,
    checkNicknameAvailability,
  };

  return (
    <ProfileContext.Provider value={contextValue}>
      {children}
    </ProfileContext.Provider>
  );
};
