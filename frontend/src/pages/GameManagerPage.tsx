import { FormEvent, useEffect, useState } from "react";
import { useAxiosWithAuth } from "@/lib/utils";
import { useCallback } from "react";

type Game = {
  id: string;
  name: string;
  skillLevels: string[];
};

const GameManagerPage = () => {
  const axiosInstance = useAxiosWithAuth();

  const [games, setGames] = useState<Game[]>([]);
  const [name, setName] = useState("");
  const [skillLevels, setSkillLevels] = useState("");
  const [error, setError] = useState("");

  const fetchGames = useCallback(async () => {
    try {
      const res = await axiosInstance.get("/user/game");
      setGames(res.data);
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
    } catch (err: any) {
      setError(
        err.response?.data?.message || err.message || "Error loading games"
      );
    }
  }, [axiosInstance]);

  const handleAddGame = async (e: FormEvent) => {
    e.preventDefault();
    setError("");

    const payload = {
      name,
      skillLevels: skillLevels.split(",").map((s) => s.trim()),
    };

    try {
      const res = await axiosInstance.post("/user/game", payload);

      if (res.status !== 200) {
        const errorData = res.data;
        throw new Error(errorData.message || "Failed to add game");
      }

      const newGame: Game = res.data;
      setGames([...games, newGame]);
      setName("");
      setSkillLevels("");
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
    } catch (err: any) {
      setError(err.message);
    }
  };

  const handleDeleteGame = async (id: string) => {
    try {
      const res = await axiosInstance.delete(`/user/game/${id}`);

      if (res.status !== 200) throw new Error("Failed to delete game");

      setGames(games.filter((g) => g.id !== id));
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
    } catch (err: any) {
      setError(err.message || "Delete error");
    }
  };

  useEffect(() => {
    fetchGames();
  }, [fetchGames]);

  return (
    <div className="p-4 max-w-2xl mx-auto">
      <h1 className="text-2xl font-bold mb-4">Game Manager</h1>

      {error && <p className="text-red-600 mb-4">{error}</p>}

      <form onSubmit={handleAddGame} className="space-y-4 mb-6">
        <input
          type="text"
          value={name}
          onChange={(e) => setName(e.target.value)}
          placeholder="Game name"
          className="w-full p-2 border rounded"
          required
        />
        <input
          type="text"
          value={skillLevels}
          onChange={(e) => setSkillLevels(e.target.value)}
          placeholder="Skill levels (comma-separated)"
          className="w-full p-2 border rounded"
          required
        />
        <button
          type="submit"
          className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
        >
          Add Game
        </button>
      </form>

      <ul className="space-y-3">
        {games.map((game) => (
          <li
            key={game.id}
            className="flex justify-between items-center border p-3 rounded"
          >
            <div>
              <p className="font-semibold">{game.name}</p>
              <p className="text-sm text-gray-600">
                Skill Levels: {game.skillLevels.join(", ")}
              </p>
            </div>
            <button
              onClick={() => handleDeleteGame(game.id)}
              className="text-red-500 hover:text-red-700"
            >
              Delete
            </button>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default GameManagerPage;
