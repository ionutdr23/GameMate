import { useEffect, useState } from "react";
import { useAxiosWithAuth } from "@/lib/utils";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Separator } from "@/components/ui/separator";
import type { Profile, SearchProfileResponse } from "@/types/profile";
import { useCallback } from "react";

type FriendRequest = {
  id: string;
  sender: Profile;
  createdAt: string;
};

export default function FriendsPage() {
  const axiosinstance = useAxiosWithAuth();
  const [friends, setFriends] = useState<SearchProfileResponse[]>([]);
  const [requests, setRequests] = useState<FriendRequest[]>([]);

  const loadData = useCallback(async () => {
    const [friendsRes, requestsRes] = await Promise.all([
      axiosinstance.get<SearchProfileResponse[]>(`/user/friends`),
      axiosinstance.get<FriendRequest[]>(`/user/friends/requests`),
    ]);

    setFriends(friendsRes.data);
    setRequests(requestsRes.data);
  }, [axiosinstance]);

  useEffect(() => {
    loadData();
  }, [loadData]);

  const respondToRequest = async (id: string, accept: boolean) => {
    await axiosinstance.post(`/user/friends/request/${id}/respond`, null, {
      params: { accept },
    });
    await loadData();
  };

  const removeFriend = async (friendId: string) => {
    await axiosinstance.delete(`/user/friends/unfriend`, {
      params: { friendProfileId: friendId },
    });
    await loadData();
  };

  return (
    <div className="container py-8 space-y-8">
      <h2 className="text-2xl font-bold">Friends</h2>
      <div className="grid gap-4">
        {friends.length === 0 ? (
          <p className="text-muted-foreground">You have no friends yet.</p>
        ) : (
          friends.map((friend) => (
            <Card key={friend.profileId}>
              <CardContent className="flex justify-between items-center p-4">
                <div className="flex items-center space-x-3">
                  <img
                    src={
                      friend.avatarUrl ||
                      "https://gamemate-assets.ams3.cdn.digitaloceanspaces.com/gamemate-assets/avatars/blank-profile-picture.png"
                    }
                    className="w-10 h-10 rounded-full object-cover"
                    alt={`${friend.nickname}'s avatar`}
                  />
                  <span className="font-medium">{friend.nickname}</span>
                </div>
                <Button
                  variant="destructive"
                  size="sm"
                  onClick={() => removeFriend(friend.profileId)}
                >
                  Unfriend
                </Button>
              </CardContent>
            </Card>
          ))
        )}
      </div>

      <Separator />

      <h2 className="text-2xl font-bold">Friend Requests</h2>
      <div className="grid gap-4">
        {requests.length === 0 ? (
          <p className="text-muted-foreground">No incoming requests.</p>
        ) : (
          requests.map((req) => (
            <Card key={req.id}>
              <CardContent className="flex justify-between items-center p-4">
                <div className="flex items-center space-x-3">
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
    </div>
  );
}
