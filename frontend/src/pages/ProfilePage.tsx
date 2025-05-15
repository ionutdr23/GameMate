import { Card, CardContent } from "@/components/ui/card";
import { useProfile } from "@/hooks/useProfile";
import { useUserProfile } from "@/hooks/useUserProfile";
import { useParams } from "react-router-dom";
import { AvailabilityDialog } from "@/components/modals/AvailabilityDialog";
import { GameProfilesDialog } from "@/components/modals/GameProfilesDialog";
import Loading from "@/components/Loading";

const ProfilePage = () => {
  const { userId } = useParams();
  const isOwnProfile = !userId;

  const { profile, isLoading, error } = isOwnProfile
    ? // eslint-disable-next-line react-hooks/rules-of-hooks
      useProfile()
    : // eslint-disable-next-line react-hooks/rules-of-hooks
      useUserProfile(userId);

  if (isLoading) return <Loading />;
  if (error || !profile)
    return <p className="text-center text-destructive">Profile not found.</p>;

  return (
    <div className="p-8 max-w-4xl mx-auto space-y-10">
      {/* Profile Header */}
      <Card>
        <CardContent className="flex flex-col sm:flex-row sm:items-center sm:space-x-6 space-y-4 sm:space-y-0 p-6">
          <img
            src={profile?.avatarUrl}
            alt="Avatar"
            className="w-28 h-28 rounded-full shadow-md object-cover"
          />
          <div>
            <h2 className="text-3xl font-bold">{profile?.nickname}</h2>
            <p className="text-muted-foreground">{profile?.bio}</p>
            <p className="text-sm text-muted-foreground">{profile?.location}</p>
            <p className="text-sm text-muted-foreground">
              Joined in{" "}
              {profile?.createdAt.toLocaleString("default", {
                month: "long",
              })}{" "}
              {profile?.createdAt.getFullYear()}
            </p>
          </div>
        </CardContent>
      </Card>

      {/* Availability */}
      <Card>
        <CardContent className="p-6 space-y-4">
          <div className="flex justify-between items-center">
            <h3 className="text-xl font-semibold">Availability</h3>
            {isOwnProfile && (
              <AvailabilityDialog
                initialAvailability={profile?.availability}
                onSave={(newAvailability) => {
                  console.log(newAvailability);
                }}
              />
            )}
          </div>
          {(profile?.availability?.length ?? 0) > 0 ? (
            <ul className="list-disc ml-5 text-muted-foreground">
              {profile?.availability?.map((slot, i) => (
                <li key={i}>
                  {slot.day}: {slot.from} – {slot.to}
                </li>
              ))}
            </ul>
          ) : (
            <p className="text-muted-foreground">No availability set yet.</p>
          )}
        </CardContent>
      </Card>

      {/* Game Profiles */}
      <Card>
        <CardContent className="p-6 space-y-4">
          <div className="flex justify-between items-center">
            <h3 className="text-xl font-semibold">Game Profiles</h3>
            {isOwnProfile && (
              <GameProfilesDialog
                initialProfiles={profile?.gameProfiles}
                onSave={(updatedProfiles) => {
                  // Do something with the cleaned list
                  console.log(updatedProfiles);
                }}
              />
            )}
          </div>
          {(profile?.gameProfiles?.length ?? 0) > 0 ? (
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              {profile?.gameProfiles?.map((game, i) => (
                <Card key={i}>
                  <CardContent className="p-4 space-y-2">
                    <div className="flex items-center space-x-4">
                      <img
                        src={game.icon}
                        className="w-10 h-10 object-contain"
                        alt={game.name}
                      />
                      <h4 className="text-lg font-bold">{game.name}</h4>
                    </div>
                    <p className="text-sm text-muted-foreground">
                      Skill Level: {game.skill}
                    </p>
                    <p className="text-sm text-muted-foreground">
                      Playstyles: {game.playstyles.join(", ")}
                    </p>
                    <p className="text-sm text-muted-foreground">
                      Platforms: {game.platforms.join(", ")}
                    </p>
                  </CardContent>
                </Card>
              ))}
            </div>
          ) : (
            <p className="text-muted-foreground">
              No game profiles available yet for this user.
            </p>
          )}
        </CardContent>
      </Card>
    </div>
  );
};

export default ProfilePage;
