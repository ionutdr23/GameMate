import { Card, CardContent } from "../components/ui/card";

const ProfilePage = () => {
  const profile = {
    displayName: "DragonSlayer77",
    avatar: "/avatar.png",
    bio: "Casual gamer with a love for strategy titles.",
    country: "Netherlands",
    city: "Eindhoven",
    createdAt: new Date("2022-04-01"),
    availability: [
      { day: "Monday", from: "18:00", to: "22:00" },
      { day: "Saturday", from: "14:00", to: "20:00" },
    ],
    gameProfiles: [
      {
        name: "League of Legends",
        icon: "/lol.png",
        skill: "Silver IV",
        playstyles: ["defensive"],
        platforms: ["PC"],
      },
      {
        name: "Fortnite",
        icon: "/fortnite.png",
        skill: "Gold II",
        playstyles: ["aggressive", "builder"],
        platforms: ["PS5"],
      },
    ],
  };

  return (
    <div className="p-8 max-w-4xl mx-auto">
      <div className="flex space-x-6 items-center">
        <img
          src={profile.avatar}
          alt="Avatar"
          className="w-28 h-28 rounded-full shadow-md"
        />
        <div>
          <h2 className="text-3xl font-bold">{profile.displayName}</h2>
          <p className="text-gray-400">{profile.bio}</p>
          <p>
            {profile.city}, {profile.country}
          </p>
          <p className="text-sm text-gray-500">
            Joined us in{" "}
            {profile.createdAt.toLocaleString("default", { month: "long" })}{" "}
            {profile.createdAt.getFullYear()}
          </p>
        </div>
      </div>

      <div className="mt-10">
        <h3 className="text-xl font-semibold mb-2">Availability</h3>
        <ul className="list-disc ml-5 text-gray-300">
          {profile.availability.map((slot, i) => (
            <li key={i}>
              {slot.day}: {slot.from} - {slot.to}
            </li>
          ))}
        </ul>
      </div>

      <div className="mt-10">
        <h3 className="text-xl font-semibold mb-2">Game Profiles</h3>
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          {profile.gameProfiles.map((game, i) => (
            <Card key={i} className="bg-gray-800 text-white">
              <CardContent className="p-4 space-y-2">
                <div className="flex items-center space-x-4">
                  <img src={game.icon} className="w-10 h-10" alt={game.name} />
                  <h4 className="text-lg font-bold">{game.name}</h4>
                </div>
                <p className="text-sm">Skill Level: {game.skill}</p>
                <p className="text-sm">
                  Playstyles: {game.playstyles.join(", ")}
                </p>
                <p className="text-sm">
                  Platforms: {game.platforms.join(", ")}
                </p>
              </CardContent>
            </Card>
          ))}
        </div>
      </div>
    </div>
  );
};

export default ProfilePage;
