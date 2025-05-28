import { Card, CardContent } from "@/components/ui/card";
import { useProfile } from "@/hooks/useProfile";
import { useUserProfile } from "@/hooks/useUserProfile";
import { useParams } from "react-router-dom";
import { GameProfilesDialog } from "@/components/modals/GameProfilesDialog";
import Loading from "@/components/Loading";
import { useGameProfileManager } from "@/hooks/useGameProfileManager";

const ProfilePage = () => {
  const { id } = useParams();
  const isOwnProfile = !id;

  const { profile, isLoading, error } = isOwnProfile
    ? // eslint-disable-next-line react-hooks/rules-of-hooks
      useProfile()
    : // eslint-disable-next-line react-hooks/rules-of-hooks
      useUserProfile(id);

  const { syncProfiles } = useGameProfileManager(profile?.gameProfiles);

  if (isLoading) return <Loading />;

  if (error || !profile)
    return <p className="text-center text-destructive">Profile not found.</p>;

  return (
    <div className="p-8 max-w-4xl mx-auto space-y-10">
      {/* Profile Header */}
      <Card className="bg-secondary-background">
        <CardContent className="flex flex-col sm:flex-row sm:items-center sm:space-x-6 space-y-4 sm:space-y-0 p-3 mx-3 justify-between">
          <div className="flex flex-row items-center space-x-4">
            <img
              src={profile?.avatarUrl}
              alt="Avatar"
              className="w-28 h-28 rounded-full shadow-md object-cover"
            />
            <div>
              <h2 className="text-3xl font-bold">{profile?.nickname}</h2>
              <p className="text-muted-foreground">{profile?.bio}</p>
              <p className="text-sm text-muted-foreground">
                {profile?.location}
              </p>
              <p className="text-sm text-muted-foreground">
                Joined in{" "}
                {profile?.createdAt.toLocaleString("default", {
                  month: "long",
                })}{" "}
                {profile?.createdAt.getFullYear()}
              </p>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Game Profiles */}
      <Card className="bg-secondary-background">
        <CardContent className="px-6 space-y-4">
          <div className="flex justify-between items-center">
            <h3 className="text-xl font-semibold">Games Played</h3>
            {isOwnProfile && (
              <GameProfilesDialog
                initialProfiles={profile?.gameProfiles}
                onSave={syncProfiles}
              />
            )}
          </div>
          {(profile?.gameProfiles?.length ?? 0) > 0 ? (
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              {profile?.gameProfiles?.map((profile, i) => (
                <Card key={i} className="bg-background">
                  <CardContent className="space-y-2">
                    <div className="flex items-center space-x-4">
                      <h4 className="text-lg font-bold">{profile.game.name}</h4>
                    </div>
                    <p className="text-sm text-muted-foreground">
                      Skill Level: {profile.skillLevel}
                    </p>
                    <p className="text-sm text-muted-foreground">
                      Playstyles: {profile.playstyles.join(", ")}
                    </p>
                    <p className="text-sm text-muted-foreground">
                      Platforms: {profile.platforms.join(", ")}
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
