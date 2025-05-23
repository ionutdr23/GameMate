import { useFetchWithAuth } from "@/lib/utils";
import { GameProfile, GameProfileRequest } from "@/types/profile";

export function useGameProfileManager(existing: GameProfile[] = []) {
  const fetchWithAuth = useFetchWithAuth();

  const syncProfiles = async (updated: GameProfileRequest[]) => {
    const toCreate = updated.filter(
      (u) => !existing.some((e) => e.game.id === u.gameId)
    );
    const toUpdate = updated.filter((u) => {
      const existingMatch = existing.find((e) => e.game.id === u.gameId);
      return (
        existingMatch &&
        (existingMatch.skillLevel !== u.skillLevel ||
          JSON.stringify(existingMatch.playstyles) !==
            JSON.stringify(u.playstyles) ||
          JSON.stringify(existingMatch.platforms) !==
            JSON.stringify(u.platforms))
      );
    });
    const toDelete = existing.filter(
      (e) => !updated.some((u) => u.gameId === e.game.id)
    );

    await Promise.all([
      ...toCreate.map((profile) =>
        fetchWithAuth("/user/profile/game", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(profile),
        })
      ),
      ...toUpdate.map((profile) =>
        fetchWithAuth("/user/profile/game", {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(profile),
        })
      ),
      ...toDelete.map((profile) =>
        fetchWithAuth(`/user/profile/game/${profile.id}`, {
          method: "DELETE",
        })
      ),
    ]);
  };

  return { syncProfiles };
}
