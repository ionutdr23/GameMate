import { Routes, Route } from "react-router-dom";

// Authentication
import { ProtectedRoute } from "./auth/ProtectedRoute";
import AuthCallback from "./auth/AuthCallback";

// Pages
import LandingPage from "./pages/LandingPage";
import ProfilePage from "./pages/ProfilePage";
import Feed from "./pages/Feed";
import MainLayout from "./components/MainLayout";
import CreateProfilePage from "./pages/CreateProfilePage";
import { useEffect } from "react";

function App() {
  useEffect(() => {
    document.documentElement.classList.add("dark");
  }, []);

  return (
    <Routes>
      {/* Public Routes */}
      <Route path="/" element={<LandingPage />} />
      {/* Not-Authenticated Routes */}
      <Route path="/auth/callback" element={<AuthCallback />} />
      {/* Authenticated Routes */}
      <Route path="/" element={<ProtectedRoute />}>
        <Route path="/create_profile" element={<CreateProfilePage />} />
        <Route path="/" element={<MainLayout />}>
          <Route path="/profile" element={<ProfilePage />} />
          <Route path="/profile/:userId" element={<ProfilePage />} />
          <Route path="/feed" element={<Feed />} />
        </Route>
      </Route>
      {/* Catch-all Route */}
    </Routes>
  );
}

export default App;
