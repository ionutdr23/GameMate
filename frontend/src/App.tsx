import { Routes, Route } from "react-router-dom";

// Authentication
import { ProtectedRoute } from "./auth/ProtectedRoute";
import AuthCallback from "./auth/AuthCallback";

// Pages
import LandingPage from "./pages/LandingPage";
import ProfilePage from "./pages/ProfilePage";
import Feed from "./pages/Feed";
import Layout from "./components/Layout";

function App() {
  return (
    <Routes>
      {/* Public Routes */}
      <Route path="/" element={<LandingPage />} />
      {/* Not-Authenticated Routes */}
      <Route path="/auth/callback" element={<AuthCallback />} />
      {/* Authenticated Routes */}
      <Route path="/" element={<Layout />}>
        <Route path="/" element={<ProtectedRoute />}>
          <Route path="/profile" element={<ProfilePage />} />
          <Route path="/profile/feed" element={<Feed own />} />
          <Route path="/feed" element={<Feed />} />
        </Route>
      </Route>
      {/* Catch-all Route */}
    </Routes>
  );
}

export default App;
