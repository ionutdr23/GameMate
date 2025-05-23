import { useCallback, useEffect, useState } from "react";
import { ProfileContext, ProfileContextType } from "./ProfileContext";
import { useFetchWithAuth } from "@/lib/utils";
import type { Profile } from "@/types/profile";

export const ProfileProvider = ({
  children,
}: {
  children: React.ReactNode;
}) => {
  const fetchWithAuth = useFetchWithAuth();
  const [profile, setProfile] = useState<Profile | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const refetch = useCallback(async () => {
    setIsLoading(true);
    setError(null);

    try {
      const res = await fetchWithAuth("/user/profile/me");
      if (res.status === 404) {
        setProfile(null);
        return;
      }
      if (!res.ok) {
        throw new Error("Failed to load profile");
      }
      const data = await res.json();
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
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const uploadAvatar = async (file: File): Promise<string> => {
    const formData = new FormData();
    formData.append("file", file);

    const res = await fetchWithAuth("/user/profile/avatar", {
      method: "POST",
      body: formData,
    });

    if (!res.ok) {
      throw new Error("Failed to upload avatar");
    }

    const data = await res.json();
    await refetch();
    return data.avatarUrl as string;
  };

  const updateProfile = async (data: Partial<Profile>) => {
    const res = await fetchWithAuth("/user/profile", {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
    });

    if (!res.ok) {
      throw new Error("Failed to update profile");
    }

    const updatedProfile = await res.json();
    setProfile({
      ...updatedProfile,
      createdAt: new Date(updatedProfile.createdAt),
    });
  };

  useEffect(() => {
    refetch();
  }, [refetch]);

  const contextValue: ProfileContextType = {
    profile,
    isLoading,
    error,
    refetch,
    uploadAvatar,
    updateProfile,
  };

  return (
    <ProfileContext.Provider value={contextValue}>
      {children}
    </ProfileContext.Provider>
  );
};
