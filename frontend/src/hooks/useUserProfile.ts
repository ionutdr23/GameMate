import { useCallback, useEffect, useState } from "react";
import { useFetchWithAuth } from "@/lib/utils";
import { Profile } from "@/types/profile";

export function useUserProfile(profileId: string | undefined) {
  const fetchWithAuth = useFetchWithAuth();
  const [profile, setProfile] = useState<Profile | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchProfile = useCallback(async () => {
    if (!profileId) return;
    setIsLoading(true);
    try {
      const res = await fetchWithAuth(`/user/profile/${profileId}`);
      if (!res.ok) throw new Error("Profile not found");
      const data = await res.json();
      const parsed = {
        ...data,
        createdAt: new Date(data.createdAt),
      };
      setProfile(parsed);
    } catch (err) {
      console.error("Error:", err);
      setError("Could not load profile.");
    } finally {
      setIsLoading(false);
    }
  }, [profileId, fetchWithAuth]);

  useEffect(() => {
    fetchProfile();
  }, [fetchProfile]);

  return { profile, isLoading, error };
}
