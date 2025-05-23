import React from "react";
import { Outlet } from "react-router-dom";
import NavBar from "./NavBar";

const MainLayout: React.FC = () => {
  return (
    <div className="min-h-screen flex flex-col bg-background text-foreground">
      <header>
        <NavBar />
      </header>

      <main className="flex-1 container mx-auto px-4 py-6">
        <Outlet />
      </main>

      <footer className="bg-secondary-background py-5 text-center text-sm text-muted-foreground">
        GameMate © {new Date().getFullYear()} — All rights reserved.
      </footer>
    </div>
  );
};

export default MainLayout;
