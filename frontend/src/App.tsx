import { Routes, Route, Outlet } from "react-router-dom";

// Authentication
import { ProtectedRoute } from "./auth/ProtectedRoute";
import AuthCallback from "./auth/AuthCallback";

// Pages
import LandingPage from "./pages/LandingPage";
import ProfilePage from "./pages/ProfilePage";
import SettingsPage from "./pages/SettingsPage";
import Feed from "./pages/Feed";
import MainLayout from "./components/MainLayout";
import CreateProfilePage from "./pages/CreateProfilePage";
import GameManagerPage from "./pages/GameManagerPage";
import UpdateProfilePage from "./pages/UpdateProfilePage";
import { ProfileProvider } from "./context/ProfileProvider";
import { NotFoundPage } from "./pages/NotFoundPage";
import { UnauthorizedPage } from "./pages/UnauthorizedPage";

function App() {
  return (
    <ProfileProvider>
      <Routes>
        {/* Public Routes */}
        <Route path="/landing" element={<LandingPage />} />
        <Route path="/auth/callback" element={<AuthCallback />} />
        {/* Authenticated Routes */}
        <Route path="/" element={<ProtectedRoute />}>
          <Route path="/profile/create" element={<CreateProfilePage />} />
          <Route path="/" element={<MainLayout />}>
            <Route path="/profile" element={<Outlet />}>
              <Route index element={<ProfilePage />} />
              <Route path=":id" element={<ProfilePage />} />
              <Route path="edit" element={<UpdateProfilePage />} />
              <Route path="settings" element={<SettingsPage />} />
            </Route>
            <Route path="/" element={<Feed />} />
          </Route>
        </Route>
        <Route
          path="/"
          element={<ProtectedRoute requiredRoles={["Moderator"]} />}
        >
          <Route path="/" element={<MainLayout />}>
            <Route path="/games" element={<GameManagerPage />} />
          </Route>
        </Route>
        {/* Unauthorized Route */}
        <Route path="/unauthorized" element={<UnauthorizedPage />} />
        {/* Catch-all Route */}
        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </ProfileProvider>
  );
}

export default App;
