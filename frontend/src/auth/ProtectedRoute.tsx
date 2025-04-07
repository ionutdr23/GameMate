import { useAuth } from "./useAuth";
import { Navigate, Outlet } from "react-router-dom";

export const ProtectedRoute = () => {
  const { isAuthenticated, isLoading } = useAuth();

  if (isLoading)
    return <div className="text-white p-4">Loading session...</div>;

  if (!isAuthenticated) return <Navigate to="/" replace />;
  return <Outlet />;
};
