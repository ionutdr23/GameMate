import React from "react";
import NavBar from "./NavBar";
import { Outlet } from "react-router-dom";

const Layout: React.FC = () => {
  return (
    <div className="min-h-screen flex flex-col">
      <NavBar />
      <main className="flex-1 bg-gray-700">
        <Outlet />
      </main>
      <footer className="py-4 text-center text-sm text-gray-300 bg-[#181c2c]">
        GameMate © {new Date().getFullYear()} - All rights reserved.
      </footer>
    </div>
  );
};

export default Layout;
