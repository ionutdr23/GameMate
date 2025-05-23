// Fixed and cleaned version of GameProfilesDialog component
import {
  Dialog,
  DialogTrigger,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
  DialogFooter,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import {
  Select,
  SelectTrigger,
  SelectValue,
  SelectContent,
  SelectItem,
} from "@/components/ui/select";
import { useEffect, useState } from "react";
import { X } from "lucide-react";
import { GameProfile, GameProfileRequest } from "@/types/profile";
import { useFetchWithAuth } from "@/lib/utils";

type GameOption = {
  id: string;
  name: string;
  skillLevels: string[];
};

export function GameProfilesDialog({
  initialProfiles = [],
  onSave,
}: {
  initialProfiles?: GameProfile[];
  onSave: (updated: GameProfileRequest[]) => Promise<void>;
}) {
  const [profiles, setProfiles] = useState<GameProfile[]>(initialProfiles);
  const [games, setGames] = useState<GameOption[]>([]);
  const [availablePlatforms, setAvailablePlatforms] = useState<string[]>([]);
  const [availablePlaystyles, setAvailablePlaystyles] = useState<string[]>([]);
  const fetchWithAuth = useFetchWithAuth();

  useEffect(() => {
    const fetchInformation = async () => {
      try {
        const [gamesRes, platformsRes, playstylesRes] = await Promise.all([
          fetchWithAuth("/user/game"),
          fetchWithAuth("/user/meta/platforms"),
          fetchWithAuth("/user/meta/playstyles"),
        ]);
        const gamesData = await gamesRes.json();
        setGames(gamesData);
        setAvailablePlatforms(await platformsRes.json());
        setAvailablePlaystyles(await playstylesRes.json());
      } catch (err) {
        console.error("Failed to fetch information:", err);
      }
    };
    fetchInformation();
  }, [fetchWithAuth]);

  const updateProfile = (
    index: number,
    key: keyof GameProfile | "game.id" | "game.name" | "game.skillLevels",
    value: any
  ) => {
    setProfiles((prev) => {
      const updated = [...prev];
      const profile = { ...updated[index] };

      if (key.startsWith("game.")) {
        const subKey = key.split(".")[1] as keyof GameProfile["game"];
        profile.game = { ...profile.game, [subKey]: value };
      } else {
        profile[key as keyof GameProfile] = value;
      }

      updated[index] = profile;
      return updated;
    });
  };

  const addProfile = () => {
    setProfiles((prev) => [
      ...prev,
      {
        id: "",
        game: { id: "", name: "", skillLevels: [] },
        skillLevel: "",
        playstyles: [],
        platforms: [],
      },
    ]);
  };

  const removeProfile = (index: number) => {
    setProfiles((prev) => prev.filter((_, i) => i !== index));
  };

  const toggleMultiValue = (
    index: number,
    key: "playstyles" | "platforms",
    value: string
  ) => {
    const current = profiles[index][key];
    const updated = current.includes(value)
      ? current.filter((v) => v !== value)
      : [...current, value];
    updateProfile(index, key, updated);
  };

  const isProfileValid = (p: GameProfile) =>
    !!p.game.id &&
    !!p.skillLevel &&
    p.platforms.length > 0 &&
    p.playstyles.length > 0;

  const allGamesUsed = games.every((game) =>
    profiles.some((p) => p.game.id === game.id)
  );

  const handleSave = async () => {
    const validProfiles = profiles.filter(isProfileValid);
    const payload: GameProfileRequest[] = validProfiles.map((p) => ({
      gameId: p.game.id,
      skillLevel: p.skillLevel,
      playstyles: p.playstyles,
      platforms: p.platforms,
    }));

    await onSave(payload);
    window.location.reload();
  };

  return (
    <Dialog>
      <DialogTrigger asChild>
        <Button variant="outline" size="sm">
          Edit
        </Button>
      </DialogTrigger>
      <DialogContent className="w-[900px] max-h-[80vh] overflow-auto">
        <DialogHeader>
          <DialogTitle>Edit Game Profiles</DialogTitle>
          <DialogDescription>
            Add or modify your game profiles. Each game can have one entry.
          </DialogDescription>
        </DialogHeader>

        <div className="space-y-6">
          {profiles.map((profile, index) => {
            const skillOptions = profile.game.skillLevels || [];

            return (
              <div key={index} className="border p-4 rounded-md relative">
                <Button
                  variant="ghost"
                  size="icon"
                  className="absolute top-2 right-2"
                  onClick={() => removeProfile(index)}
                >
                  <X className="w-4 h-4" />
                </Button>

                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <Label className="mb-1">Game</Label>
                    {!profile.game.id && (
                      <p className="text-red-500 text-sm mt-1">
                        Game is required.
                      </p>
                    )}
                    <Select
                      value={profile.game.id}
                      onValueChange={(gameId) => {
                        const game = games.find((g) => g.id === gameId);
                        if (game) {
                          updateProfile(index, "game.id", game.id);
                          updateProfile(index, "game.name", game.name);
                          updateProfile(
                            index,
                            "game.skillLevels",
                            game.skillLevels
                          );
                          updateProfile(index, "skillLevel", "");
                        }
                      }}
                    >
                      <SelectTrigger>
                        <SelectValue placeholder="Select a game" />
                      </SelectTrigger>
                      <SelectContent>
                        {games
                          .filter(
                            (game) =>
                              profile.game.id === game.id ||
                              !profiles.some(
                                (p, i) => i !== index && p.game.id === game.id
                              )
                          )
                          .map((game) => (
                            <SelectItem key={game.id} value={game.id}>
                              {game.name}
                            </SelectItem>
                          ))}
                      </SelectContent>
                    </Select>
                  </div>

                  <div>
                    <Label className="mb-1">Skill Level</Label>
                    {!profile.skillLevel && (
                      <p className="text-red-500 text-sm mt-1">
                        Skill level is required.
                      </p>
                    )}
                    <Select
                      value={profile.skillLevel}
                      disabled={!skillOptions.length}
                      onValueChange={(value) =>
                        updateProfile(index, "skillLevel", value)
                      }
                    >
                      <SelectTrigger>
                        <SelectValue placeholder="Select a skill level" />
                      </SelectTrigger>
                      <SelectContent>
                        {skillOptions.map((skill) => (
                          <SelectItem key={skill} value={skill}>
                            {skill}
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                  </div>

                  <div>
                    <Label>Playstyles</Label>
                    {profile.playstyles.length === 0 && (
                      <p className="text-red-500 text-sm mt-1">
                        Select at least one playstyle.
                      </p>
                    )}
                    <div className="flex flex-wrap gap-2 mt-1">
                      {availablePlaystyles.map((style) => (
                        <Button
                          key={style}
                          type="button"
                          variant={
                            profile.playstyles.includes(style)
                              ? "default"
                              : "outline"
                          }
                          size="sm"
                          onClick={() =>
                            toggleMultiValue(index, "playstyles", style)
                          }
                        >
                          {style}
                        </Button>
                      ))}
                    </div>
                  </div>

                  <div>
                    <Label>Platforms</Label>
                    {profile.platforms.length === 0 && (
                      <p className="text-red-500 text-sm mt-1">
                        Select at least one platform.
                      </p>
                    )}
                    <div className="flex flex-wrap gap-2 mt-1">
                      {availablePlatforms.map((platform) => (
                        <Button
                          key={platform}
                          type="button"
                          variant={
                            profile.platforms.includes(platform)
                              ? "default"
                              : "outline"
                          }
                          size="sm"
                          onClick={() =>
                            toggleMultiValue(index, "platforms", platform)
                          }
                        >
                          {platform}
                        </Button>
                      ))}
                    </div>
                  </div>
                </div>
              </div>
            );
          })}

          <Button
            onClick={addProfile}
            variant="secondary"
            disabled={allGamesUsed}
          >
            Add Game Profile
          </Button>
        </div>

        <DialogFooter className="pt-4">
          <Button
            onClick={handleSave}
            disabled={profiles.some((p) => !isProfileValid(p))}
          >
            Save
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
