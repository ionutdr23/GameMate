"use client";

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
import { GameProfile } from "@/types/profile";

const availablePlaystyles = [
  "Aggressive",
  "Defensive",
  "Supportive",
  "Tactical",
];
const availablePlatforms = ["PC", "PlayStation", "Xbox", "Switch", "Mobile"];

type GameOption = {
  id: string;
  name: string;
  icon: string;
};

export function GameProfilesDialog({
  initialProfiles = [],
  onSave,
}: {
  initialProfiles?: GameProfile[];
  onSave: (updated: GameProfile[]) => void;
}) {
  const [profiles, setProfiles] = useState<GameProfile[]>(initialProfiles);
  const [games, setGames] = useState<GameOption[]>([]);
  const [skillsMap, setSkillsMap] = useState<Record<string, string[]>>({});

  useEffect(() => {
    fetch("/user/games")
      .then((res) => res.json())
      .then(setGames)
      .catch(console.error);
  }, []);

  const fetchSkillsForGame = async (gameId: string) => {
    if (skillsMap[gameId]) return;
    const res = await fetch(`/user/games/${gameId}/skill`);
    const skills = await res.json();
    setSkillsMap((prev) => ({ ...prev, [gameId]: skills }));
  };

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const updateProfile = (index: number, key: keyof GameProfile, value: any) => {
    setProfiles((prev) => {
      const updated = [...prev];
      updated[index] = { ...updated[index], [key]: value };
      return updated;
    });
  };

  const addProfile = () => {
    setProfiles((prev) => [
      ...prev,
      { name: "", icon: "", skill: "", playstyles: [], platforms: [] },
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

  const handleSave = () => {
    const validProfiles = profiles.filter((p) => p.name && p.skill);
    const unique = Object.values(
      validProfiles.reduce((acc, p) => {
        acc[p.name.toLowerCase()] = p;
        return acc;
      }, {} as Record<string, GameProfile>)
    );
    onSave(unique);
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
            const selectedGame = games.find((g) => g.name === profile.name);
            const gameId = selectedGame?.id;
            const skillOptions = gameId ? skillsMap[gameId] ?? [] : [];

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
                    <Select
                      value={profile.name}
                      onValueChange={(gameName) => {
                        const game = games.find((g) => g.name === gameName);
                        if (game) {
                          updateProfile(index, "name", game.name);
                          updateProfile(index, "icon", game.icon);
                          updateProfile(index, "skill", ""); // reset skill
                          fetchSkillsForGame(game.id);
                        }
                      }}
                    >
                      <SelectTrigger>
                        <SelectValue placeholder="Select a game" />
                      </SelectTrigger>
                      <SelectContent>
                        {games.map((game) => (
                          <SelectItem key={game.id} value={game.name}>
                            {game.name}
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                  </div>

                  <div>
                    <Label className="mb-1">Skill Level</Label>
                    <Select
                      value={profile.skill}
                      disabled={!skillOptions.length}
                      onValueChange={(value) =>
                        updateProfile(index, "skill", value)
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
          <Button onClick={addProfile} variant="secondary">
            Add Game Profile
          </Button>
        </div>

        <DialogFooter className="pt-4">
          <Button onClick={handleSave}>Save</Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
