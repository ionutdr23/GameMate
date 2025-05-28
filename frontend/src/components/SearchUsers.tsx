import { useState, useRef, useEffect } from "react";
import { useAxiosWithAuth, cn } from "@/lib/utils";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { SearchProfileResponse } from "@/types/profile";

export function SearchUsers() {
  const [query, setQuery] = useState("");
  const [isOpen, setIsOpen] = useState(false);
  const [debounceTimer, setDebounceTimer] = useState<NodeJS.Timeout | null>(
    null
  );
  const dropdownRef = useRef<HTMLDivElement>(null);
  const axios = useAxiosWithAuth();
  const queryClient = useQueryClient();

  const fetchUsers = async (): Promise<SearchProfileResponse[]> => {
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
    staleTime: 60_000, // 1 min cache
  });

  const { mutate } = useMutation({
    mutationFn: (id: string) =>
      axios.post(`/user/friends/request`, null, {
        params: { receiverProfileId: id },
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["searchUsers", query] });
    },
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
          {users.map((user) => (
            <div
              key={user.profileId}
              className="flex items-center justify-between px-4 py-2 hover:bg-muted cursor-pointer"
            >
              <div className="flex items-center space-x-2">
                <img
                  src={
                    user.avatarUrl ||
                    "https://gamemate-assets.ams3.cdn.digitaloceanspaces.com/gamemate-assets/avatars/blank-profile-picture.png"
                  }
                  alt="avatar"
                  className="w-8 h-8 rounded-full object-cover"
                />
                <span>{user.nickname}</span>
              </div>
              {!user.isFriend ? (
                <Button
                  size="sm"
                  variant="secondary"
                  onClick={() => mutate(user.profileId)}
                >
                  Add
                </Button>
              ) : (
                <span className="text-sm text-green-600">✓</span>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
