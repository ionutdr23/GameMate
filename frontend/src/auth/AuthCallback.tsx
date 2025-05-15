import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "./useAuth";
import Loading from "@/components/Loading";

const AuthCallback = () => {
  const { isLoading, isAuthenticated, error, getIdTokenClaims } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    const handleRedirect = async () => {
      if (!isLoading && isAuthenticated) {
        const claims = await getIdTokenClaims();
        const isNewUser = claims?.["new_user"];
        if (isNewUser) {
          navigate("/create_profile");
        } else {
          navigate("/profile");
        }
      }
    };

    handleRedirect();
  }, [isLoading, isAuthenticated, getIdTokenClaims, navigate]);

  if (error) return <p className="text-red-400">Error: {error.message}</p>;
  return <Loading />;
};

export default AuthCallback;
