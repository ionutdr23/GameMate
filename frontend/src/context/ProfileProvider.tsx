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
    try {
      const res = await fetchWithAuth("/user/profile");
      if (!res.ok) throw new Error("Failed to load profile");
      const data = await res.json();
      const parsed = {
        ...data,
        createdAt: new Date(data.createdAt),
      };
      setProfile(parsed);
    } catch (err) {
      setError((err as Error).message);
    } finally {
      setIsLoading(false);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  useEffect(() => {
    refetch();
  }, [refetch]);

  const contextValue: ProfileContextType = {
    profile,
    isLoading,
    error,
    refetch,
  };

  return (
    <ProfileContext.Provider value={contextValue}>
      {children}
    </ProfileContext.Provider>
  );
};
