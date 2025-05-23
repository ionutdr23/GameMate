import { useState } from "react";
import { NavLink, useNavigate } from "react-router-dom";
import { AuthButton } from "../auth/AuthButton";
import { useProfile } from "@/hooks/useProfile";
import ProfilePicture from "./ProfilePicture";
import { useAuth } from "@/auth/useAuth";

const NavBar = () => {
  const { profile } = useProfile();
  const navigate = useNavigate();
  const [isOpen, setIsOpen] = useState(false);
  const { hasRole } = useAuth();

  const gamerLinks: { to: string; label: string }[] = [
    { to: "/", label: "My Feed" },
  ];
  const moderatorLinks: { to: string; label: string }[] = [
    { to: "/games", label: "Game Manager" },
  ];
  const navLinks = hasRole("Moderator")
    ? [...gamerLinks, ...moderatorLinks]
    : gamerLinks;

  return (
    <div className="bg-secondary-background">
      <nav className="h-20 flex items-center justify-between mx-5 px-4 sm:px-6 py-2 lg:mx-32 md:mx-16 sm:mx-8">
        {/* Logo */}
        <img
          src="https://gamemate-assets.ams3.cdn.digitaloceanspaces.com/gamemate-assets/icons/logo.png"
          alt="GameMate Logo"
          className="w-auto h-24 object-contain"
        />

        {/* Mobile toggle */}
        <button
          className="sm:hidden text-muted-foreground focus:outline-none"
          onClick={() => setIsOpen(!isOpen)}
        >
          ☰
        </button>

        {/* Desktop nav links */}
        <ul className="hidden sm:flex items-center gap-6">
          {navLinks.map(({ to, label }) => (
            <li key={to}>
              <NavLink
                to={to}
                className={({ isActive }) =>
                  `text-sm transition-colors ${
                    isActive
                      ? "text-primary font-medium underline underline-offset-4"
                      : "text-muted-foreground hover:text-foreground"
                  }`
                }
              >
                {label}
              </NavLink>
            </li>
          ))}
        </ul>

        {/* Desktop user section */}
        <div className="hidden sm:flex items-center gap-4">
          <ProfilePicture />
        </div>
      </nav>

      {/* Mobile menu */}
      {isOpen && (
        <div className="sm:hidden px-4 pb-4 pt-2 space-y-2 text-foreground border-border text-center">
          <ul className="space-y-2">
            {navLinks.map(({ to, label }) => (
              <li key={to}>
                <NavLink
                  to={to}
                  className={({ isActive }) =>
                    `block py-1.5 px-2 rounded-md text-sm transition ${
                      isActive
                        ? "bg-muted font-semibold"
                        : "hover:bg-muted text-muted-foreground"
                    }`
                  }
                  onClick={() => setIsOpen(false)}
                >
                  {label}
                </NavLink>
              </li>
            ))}
          </ul>
          <div className="flex justify-center items-center pt-3 gap-5">
            <img
              src={
                profile?.avatarUrl &&
                "https://gamemate-assets.ams3.cdn.digitaloceanspaces.com/gamemate-assets/avatars/blank-profile-picture.png"
              }
              alt="User Avatar"
              className="w-8 h-8 rounded-full object-cover cursor-pointer"
              onClick={() => {
                setIsOpen(false);
                navigate("/profile");
              }}
            />
            <AuthButton />
          </div>
        </div>
      )}
    </div>
  );
};

export default NavBar;
