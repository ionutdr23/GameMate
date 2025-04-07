import { Link, NavLink, useNavigate } from "react-router-dom";
import { useState } from "react";
import { AuthButton } from "../auth/AuthButton";
import { useAuth } from "../auth/useAuth";

const NavBar = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [isOpen, setIsOpen] = useState(false);

  const navLinks: { to: string; label: string }[] = [];

  return (
    <header className="bg-[#181c2c] shadow-sm">
      <nav className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-3 flex items-center justify-between">
        <Link to="/feed" className="text-white font-bold text-xl tracking-wide">
          GameMate
        </Link>
        <div className="sm:hidden">
          <button
            onClick={() => setIsOpen(!isOpen)}
            className="text-white focus:outline-none"
          >
            ☰
          </button>
        </div>
        <ul className="hidden sm:flex gap-6">
          {navLinks.map(({ to, label }) => (
            <li key={to}>
              <NavLink
                to={to}
                className={({ isActive }) =>
                  `text-white hover:text-indigo-400 transition ${
                    isActive ? "font-semibold underline underline-offset-4" : ""
                  }`
                }
              >
                {label}
              </NavLink>
            </li>
          ))}
        </ul>
        <div className="hidden sm:flex items-center gap-4">
          {user && (
            <img
              src={user.picture}
              alt="User Avatar"
              className="w-8 h-8 rounded-full cursor-pointer"
              onClick={() => navigate("/profile")}
            />
          )}
          <AuthButton />
        </div>
      </nav>

      {/* Mobile dropdown menu */}
      {isOpen && (
        <ul className="sm:hidden px-4 pb-4 space-y-2 bg-[#181c2c] border-t border-gray-700">
          {navLinks.map(({ to, label }) => (
            <li key={to}>
              <NavLink
                to={to}
                className={({ isActive }) =>
                  `block text-white py-1 px-2 rounded hover:bg-gray-800 ${
                    isActive ? "bg-gray-800 font-medium" : ""
                  }`
                }
                onClick={() => setIsOpen(false)}
              >
                {label}
              </NavLink>
            </li>
          ))}
          <li className="pt-2 border-t border-gray-700 flex items-center gap-4">
            {user && (
              <img
                src={user.picture}
                alt="User Avatar"
                className="w-8 h-8 rounded-full"
                onClick={() => navigate("/profile")}
              />
            )}
            <AuthButton />
          </li>
        </ul>
      )}
    </header>
  );
};

export default NavBar;
