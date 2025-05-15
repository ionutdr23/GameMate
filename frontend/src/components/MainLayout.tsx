import React from "react";
import { Outlet } from "react-router-dom";
import NavBar from "./NavBar";
import { ProfileProvider } from "@/context/ProfileProvider";

const MainLayout: React.FC = () => {
  return (
    <ProfileProvider>
      <div className="min-h-screen flex flex-col bg-background text-foreground">
        <header>
          <NavBar />
        </header>

        <main className="flex-1 container mx-auto px-4 py-6">
          <Outlet />
        </main>

        <footer className="py-4 text-center text-sm text-muted-foreground border-t">
          GameMate © {new Date().getFullYear()} — All rights reserved.
        </footer>
      </div>
    </ProfileProvider>
  );
};

export default MainLayout;
