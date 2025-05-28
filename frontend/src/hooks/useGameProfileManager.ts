import { useAxiosWithAuth } from "@/lib/utils";
import { GameProfile, GameProfileRequest } from "@/types/profile";

export function useGameProfileManager(existing: GameProfile[] = []) {
  const axiosInstance = useAxiosWithAuth();

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
        axiosInstance.post("/user/profile/game", profile)
      ),
      ...toUpdate.map((profile) =>
        axiosInstance.put("/user/profile/game", profile)
      ),
      ...toDelete.map((profile) =>
        axiosInstance.delete(`/user/profile/game/${profile.id}`)
      ),
    ]);
  };

  return { syncProfiles };
}
