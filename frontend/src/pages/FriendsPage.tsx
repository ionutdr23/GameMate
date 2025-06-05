import { useProfile } from "@/hooks/useProfile";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Separator } from "@/components/ui/separator";
import Loading from "@/components/Loading";
import { useAxiosWithAuth } from "@/lib/utils";
import { Link } from "react-router-dom";

export default function FriendsPage() {
  const axiosinstance = useAxiosWithAuth();
  const { profile, isLoading, error, refetch } = useProfile();

  const respondToRequest = async (id: string, accept: boolean) => {
    await axiosinstance.post(`/user/friends/request/${id}/respond`, null, {
      params: { accept },
    });
    await refetch();
  };

  const removeFriend = async (friendId: string) => {
    await axiosinstance.delete(`/user/friends/unfriend`, {
      params: { friendProfileId: friendId },
    });
    await refetch();
  };

  const deleteSentRequest = async (id: string) => {
    await axiosinstance.delete(`/user/friends/request/${id}`);
    await refetch();
  };

  if (isLoading) return <Loading />;
  if (error || !profile)
    return (
      <p className="text-center text-destructive">Could not load profile.</p>
    );

  return (
    <div className="container py-8 space-y-8">
      <h2 className="text-2xl font-bold">Friends</h2>
      <div className="grid gap-4">
        {profile.friends.length === 0 ? (
          <p className="text-muted-foreground">You have no friends yet.</p>
        ) : (
          profile.friends.map((friend) => (
            <Card key={friend.id}>
              <CardContent className="flex justify-between items-center p-4">
                <div className="flex items-center space-x-3">
                  <Link
                    to={`/profile/${friend.id}`}
                    className="flex items-center space-x-3"
                  >
                    <img
                      src={
                        friend.avatarUrl ||
                        "https://gamemate-assets.ams3.cdn.digitaloceanspaces.com/gamemate-assets/avatars/blank-profile-picture.png"
                      }
                      className="w-10 h-10 rounded-full object-cover"
                      alt={`${friend.nickname}'s avatar`}
                    />
                    <span className="font-medium">{friend.nickname}</span>
                  </Link>
                </div>
                <Button
                  variant="destructive"
                  size="sm"
                  onClick={() => removeFriend(friend.id)}
                >
                  Unfriend
                </Button>
              </CardContent>
            </Card>
          ))
        )}
      </div>

      <Separator />

      <h2 className="text-2xl font-bold">Incoming Friend Requests</h2>
      <div className="grid gap-4">
        {profile.receivedFriendRequests.length === 0 ? (
          <p className="text-muted-foreground">No incoming requests.</p>
        ) : (
          profile.receivedFriendRequests.map((req) => (
            <Card key={req.id}>
              <CardContent className="flex justify-between items-center p-4">
                <div className="flex items-center space-x-3">
                  <Link
                    to={`/profile/${req.sender.id}`}
                    className="flex items-center space-x-3"
                  >
                    <img
                      src={
                        req.sender.avatarUrl ||
                        "https://gamemate-assets.ams3.cdn.digitaloceanspaces.com/gamemate-assets/avatars/blank-profile-picture.png"
                      }
                      className="w-10 h-10 rounded-full object-cover"
                      alt={`${req.sender.nickname}'s avatar`}
                    />
                    <div>
                      <p className="font-medium">{req.sender.nickname}</p>
                      <p className="text-xs text-muted-foreground">
                        Requested on {new Date(req.createdAt).toLocaleString()}
                      </p>
                    </div>
                  </Link>
                </div>
                <div className="flex gap-2">
                  <Button
                    size="sm"
                    onClick={() => respondToRequest(req.id, true)}
                  >
                    Accept
                  </Button>
                  <Button
                    variant="secondary"
                    size="sm"
                    onClick={() => respondToRequest(req.id, false)}
                  >
                    Decline
                  </Button>
                </div>
              </CardContent>
            </Card>
          ))
        )}
      </div>

      <Separator />

      <h2 className="text-2xl font-bold">Outgoing Friend Requests</h2>
      <div className="grid gap-4">
        {profile.sentFriendRequests.length === 0 ? (
          <p className="text-muted-foreground">
            You haven't sent any friend requests.
          </p>
        ) : (
          profile.sentFriendRequests.map((req) => (
            <Card key={req.id}>
              <CardContent className="flex justify-between items-center p-4">
                <div className="flex items-center space-x-3">
                  <Link
                    to={`/profile/${req.receiver.id}`}
                    className="flex items-center space-x-3"
                  >
                    <img
                      src={
                        req.receiver.avatarUrl ||
                        "https://gamemate-assets.ams3.cdn.digitaloceanspaces.com/gamemate-assets/avatars/blank-profile-picture.png"
                      }
                      className="w-10 h-10 rounded-full object-cover"
                      alt={`${req.receiver.nickname}'s avatar`}
                    />
                    <div>
                      <p className="font-medium">{req.receiver.nickname}</p>
                      <p className="text-xs text-muted-foreground">
                        Sent on {new Date(req.createdAt).toLocaleString()}
                      </p>
                    </div>
                  </Link>
                </div>
                <Button
                  variant="destructive"
                  size="sm"
                  onClick={() => deleteSentRequest(req.id)}
                >
                  Cancel Request
                </Button>
              </CardContent>
            </Card>
          ))
        )}
      </div>
    </div>
  );
}
