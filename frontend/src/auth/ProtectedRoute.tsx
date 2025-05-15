import Loading from "@/components/Loading";
import { useAuth } from "./useAuth";
import { Navigate, Outlet } from "react-router-dom";

export const ProtectedRoute = () => {
  const { isAuthenticated, isLoading } = useAuth();

  if (isLoading) return <Loading />;

  if (!isAuthenticated) return <Navigate to="/" replace />;

  return <Outlet />;
};
