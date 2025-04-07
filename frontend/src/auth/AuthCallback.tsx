import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "./useAuth";

const AuthCallback = () => {
  const { isLoading, isAuthenticated, error } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (!isLoading) {
      if (isAuthenticated) console.log("Authenticated!");
      else console.log("Not authenticated!");
    }
  }, [isLoading, isAuthenticated, navigate]);

  if (error) return <p className="text-red-400">Error: {error.message}</p>;
  return <p className="text-white">Processing login...</p>;
};
export default AuthCallback;
