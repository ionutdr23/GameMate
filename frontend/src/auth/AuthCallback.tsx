import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "./useAuth";
import Loading from "@/components/Loading";

const AuthCallback = () => {
  const { isLoading, isAuthenticated, error, user } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    const handleRedirect = async () => {
      if (!isLoading && isAuthenticated) {
        navigate("/");
      }
    };

    handleRedirect();
  }, [isLoading, isAuthenticated, user, navigate]);

  if (error) return <p className="text-red-400">Error: {error.message}</p>;
  return <Loading />;
};

export default AuthCallback;
