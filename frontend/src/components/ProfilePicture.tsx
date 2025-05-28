import { useNavigate } from "react-router-dom";
import { useProfile } from "@/hooks/useProfile";
import {
  DropdownMenu,
  DropdownMenuTrigger,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
} from "@/components/ui/dropdown-menu";
import { AuthButton } from "../auth/AuthButton";

const ProfilePicture = () => {
  const { profile } = useProfile();
  const navigate = useNavigate();

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <img
          src={
            profile?.avatarUrl ||
            "https://gamemate-assets.ams3.cdn.digitaloceanspaces.com/gamemate-assets/avatars/blank-profile-picture.png"
          }
          alt="User Avatar"
          className="w-14 h-14 rounded-full object-cover cursor-pointer"
        />
      </DropdownMenuTrigger>

      <DropdownMenuContent align="end" className="w-48 bg-secondary-background">
        <DropdownMenuItem onClick={() => navigate("/profile")}>
          Profile
        </DropdownMenuItem>
        <DropdownMenuItem onClick={() => navigate("/profile/edit")}>
          Edit Profile
        </DropdownMenuItem>
        <DropdownMenuItem onClick={() => navigate("/profile/settings")}>
          Application Settings
        </DropdownMenuItem>
        <DropdownMenuSeparator />
        <div className="p-1 flex items-center">
          <AuthButton />
        </div>
      </DropdownMenuContent>
    </DropdownMenu>
  );
};

export default ProfilePicture;
