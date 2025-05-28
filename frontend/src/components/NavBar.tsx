import { useState } from "react";
import { NavLink } from "react-router-dom";
import { AuthButton } from "../auth/AuthButton";
import ProfilePicture from "./ProfilePicture";
import { useAuth } from "@/auth/useAuth";
import { SearchUsers } from "./SearchUsers";

const NavBar = () => {
  const [isOpen, setIsOpen] = useState(false);
  const { hasRole } = useAuth();

  const gamerLinks: { to: string; label: string }[] = [
    { to: "/", label: "My Feed" },
    { to: "/friends", label: "Friends" },
  ];
  const moderatorLinks: { to: string; label: string }[] = [
    { to: "/games", label: "Game Manager" },
  ];
  const navLinks = hasRole("Moderator")
    ? [...gamerLinks, ...moderatorLinks]
    : gamerLinks;

  return (
    <div className="bg-secondary-background">
      <nav className="h-20 flex items-center justify-between px-4 sm:px-6 lg:px-32 md:px-16 bg-secondary-background">
        {/* Logo */}
        <div className="flex items-center gap-4">
          <img
            src="https://gamemate-assets.ams3.cdn.digitaloceanspaces.com/gamemate-assets/icons/logo.png"
            alt="GameMate Logo"
            className="h-24 w-auto object-contain"
          />
        </div>

        {/* Desktop Nav */}
        <div className="hidden sm:flex items-center gap-6 w-full justify-end">
          <ul className="flex items-center gap-6">
            {navLinks.map(({ to, label }) => (
              <li key={to}>
                <NavLink
                  to={to}
                  className={({ isActive }) =>
                    `text-sm whitespace-nowrap transition-colors ${
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
          <div className="w-48">
            <SearchUsers />
          </div>
          <ProfilePicture />
        </div>

        {/* Mobile menu toggle */}
        <button
          className="sm:hidden text-muted-foreground focus:outline-none"
          onClick={() => setIsOpen(!isOpen)}
        >
          ☰
        </button>
      </nav>

      {/* Mobile menu */}
      {isOpen && (
        <div className="sm:hidden px-4 pb-4 pt-2 space-y-3 text-foreground border-border text-center">
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

          <div className="flex justify-center items-center pt-2">
            <div className="w-full max-w-xs">
              <SearchUsers />
            </div>
          </div>

          <div className="flex justify-center items-center pt-3 gap-5">
            <ProfilePicture />
            <AuthButton />
          </div>
        </div>
      )}
    </div>
  );
};

export default NavBar;
