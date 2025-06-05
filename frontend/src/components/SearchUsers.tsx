import { useState, useRef, useEffect } from "react";
import { useAxiosWithAuth, cn } from "@/lib/utils";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { useProfile } from "@/hooks/useProfile";
import { ProfilePreview } from "@/types/profile";
import { Link } from "react-router-dom";

export function SearchUsers() {
  const [query, setQuery] = useState("");
  const [isOpen, setIsOpen] = useState(false);
  const [debounceTimer, setDebounceTimer] = useState<NodeJS.Timeout | null>(
    null
  );
  const dropdownRef = useRef<HTMLDivElement>(null);
  const axios = useAxiosWithAuth();
  const queryClient = useQueryClient();
  const { profile, refetch: refetchProfile } = useProfile();

  const fetchUsers = async (): Promise<ProfilePreview[]> => {
    if (!query) return [];
    const res = await axios.get(`/user/profile/search`, {
      params: { nickname: query },
    });
    return res.data;
  };

  const { data: users = [], refetch } = useQuery({
    queryKey: ["searchUsers", query],
    queryFn: fetchUsers,
    enabled: false,
    staleTime: 60_000,
  });

  const invalidate = () => {
    queryClient.invalidateQueries({ queryKey: ["searchUsers", query] });
    refetchProfile();
  };

  const { mutate: sendRequest } = useMutation({
    mutationFn: (id: string) =>
      axios.post(`/user/friends/request`, null, {
        params: { receiverProfileId: id },
      }),
    onSuccess: invalidate,
  });

  const { mutate: cancelRequest } = useMutation({
    mutationFn: (requestId: string) =>
      axios.delete(`/user/friends/request/${requestId}`),
    onSuccess: invalidate,
  });

  const { mutate: respondToRequest } = useMutation({
    mutationFn: ({
      requestId,
      accept,
    }: {
      requestId: string;
      accept: boolean;
    }) =>
      axios.post(`/user/friends/request/${requestId}/respond`, null, {
        params: { accept },
      }),
    onSuccess: invalidate,
  });

  const handleInput = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    setQuery(value);
    if (debounceTimer) clearTimeout(debounceTimer);
    const timer = setTimeout(() => {
      setIsOpen(value.length >= 2);
      if (value.length >= 2) refetch();
    }, 300);
    setDebounceTimer(timer);
  };

  useEffect(() => {
    const handleClick = (e: MouseEvent) => {
      if (!dropdownRef.current?.contains(e.target as Node)) {
        setIsOpen(false);
      }
    };
    const handleEsc = (e: KeyboardEvent) => {
      if (e.key === "Escape") setIsOpen(false);
    };
    document.addEventListener("mousedown", handleClick);
    document.addEventListener("keydown", handleEsc);
    return () => {
      document.removeEventListener("mousedown", handleClick);
      document.removeEventListener("keydown", handleEsc);
    };
  }, []);

  return (
    <div className="relative w-full max-w-xs sm:max-w-sm" ref={dropdownRef}>
      <Input
        placeholder="Search users..."
        value={query}
        onChange={handleInput}
        className="w-full"
      />
      {isOpen && users.length > 0 && (
        <div
          className={cn(
            "absolute z-50 mt-2 bg-popover border border-border rounded-md shadow-lg max-h-96 overflow-y-auto",
            "w-full sm:w-96"
          )}
        >
          {users.map((user) => {
            const isFriend = profile?.friends.some((f) => f.id === user.id);
            const sentRequest = profile?.sentFriendRequests.find(
              (r) => r.receiver.id === user.id
            );
            const receivedRequest = profile?.receivedFriendRequests.find(
              (r) => r.sender.id === user.id
            );

            return (
              <div
                key={user.id}
                className="flex items-center justify-between px-4 py-2 hover:bg-muted cursor-pointer"
              >
                <Link
                  to={`/profile/${user.id}`}
                  className="flex items-center space-x-2"
                >
                  <img
                    src={
                      user.avatarUrl ||
                      "https://gamemate-assets.ams3.cdn.digitaloceanspaces.com/gamemate-assets/avatars/blank-profile-picture.png"
                    }
                    alt="avatar"
                    className="w-8 h-8 rounded-full object-cover"
                  />
                  <span>{user.nickname}</span>
                </Link>

                <div className="flex gap-2">
                  {isFriend ? (
                    <span className="text-sm text-muted-foreground">
                      Friend
                    </span>
                  ) : receivedRequest ? (
                    <>
                      <Button
                        size="sm"
                        onClick={() =>
                          respondToRequest({
                            requestId: receivedRequest.id,
                            accept: true,
                          })
                        }
                      >
                        Accept
                      </Button>
                      <Button
                        size="sm"
                        variant="secondary"
                        onClick={() =>
                          respondToRequest({
                            requestId: receivedRequest.id,
                            accept: false,
                          })
                        }
                      >
                        Decline
                      </Button>
                    </>
                  ) : sentRequest ? (
                    <Button
                      size="sm"
                      variant="destructive"
                      onClick={() => cancelRequest(sentRequest.id)}
                    >
                      Cancel
                    </Button>
                  ) : (
                    <Button
                      size="sm"
                      variant="secondary"
                      onClick={() => sendRequest(user.id)}
                    >
                      Add
                    </Button>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}
