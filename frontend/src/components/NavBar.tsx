import { useState } from "react";
import { NavLink, useNavigate } from "react-router-dom";
import { AuthButton } from "../auth/AuthButton";
import { useProfile } from "@/hooks/useProfile";

const NavBar = () => {
  const { profile } = useProfile();
  const navigate = useNavigate();
  const [isOpen, setIsOpen] = useState(false);

  const navLinks: { to: string; label: string }[] = [
    { to: "/feed", label: "Feed" },
  ];

  return (
    <header className="bg-background border-b shadow-sm">
      <nav className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-2 flex items-center justify-between h-16 sm:h-20">
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
          <img
            src={
              profile?.avatarUrl ||
              "https://gamemate-assets.ams3.cdn.digitaloceanspaces.com/gamemate-assets/avatars/blank-profile-picture.png"
            }
            alt="User Avatar"
            className="w-10 h-10 rounded-full object-cover cursor-pointer"
            onClick={() => navigate("/profile")}
          />
          <AuthButton />
        </div>
      </nav>

      {/* Mobile menu */}
      {isOpen && (
        <div className="sm:hidden px-4 pb-4 pt-2 space-y-2 border-t bg-background text-foreground border-border">
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
          <div className="pt-3 border-t border-border flex items-center gap-3">
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
    </header>
  );
};

export default NavBar;
