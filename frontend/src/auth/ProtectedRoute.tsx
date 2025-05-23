import Loading from "@/components/Loading";
import { useAuth } from "./useAuth";
import { useProfile } from "@/hooks/useProfile";
import { Navigate, Outlet, useLocation } from "react-router-dom";

interface ProtectedRouteProps {
  requiredRoles?: string[];
}

export const ProtectedRoute = ({ requiredRoles }: ProtectedRouteProps) => {
  const { isAuthenticated, isLoading: isAuthLoading, hasAnyRole } = useAuth();
  const { profile, isLoading: isProfileLoading } = useProfile();
  const location = useLocation();

  if (isAuthLoading || isProfileLoading) {
    return <Loading />;
  }

  if (!isAuthenticated) {
    return <Navigate to="/landing" replace />;
  }

  const isCreateProfileRoute = location.pathname === "/profile/create";

  if (!profile && !isCreateProfileRoute) {
    return <Navigate to="/profile/create" replace />;
  }

  if (profile && isCreateProfileRoute) {
    return <Navigate to="/profile" replace />;
  }

  if (requiredRoles && requiredRoles.length > 0) {
    if (!hasAnyRole(requiredRoles)) {
      return <Navigate to="/unauthorized" replace />;
    }
  }

  return <Outlet />;
};
