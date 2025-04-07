import { useNavigate } from "react-router-dom";
import { Button } from "../components/ui/button";
import { useAuth } from "../auth/useAuth";

const LandingPage = () => {
  const navigate = useNavigate();
  const { isAuthenticated, loginWithRedirect } = useAuth();

  const handleNavigate = () => {
    if (isAuthenticated) {
      navigate("/profile");
    } else {
      loginWithRedirect();
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-gray-900 to-black">
      <div className="text-center space-y-6">
        <h1 className="text-5xl font-bold text-white">Welcome to GameMate</h1>
        <p className="text-xl text-gray-300">
          Find teammates and connect with gamers worldwide.
        </p>
        <div className="flex justify-center space-x-4">
          <Button
            onClick={handleNavigate}
            className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
          >
            Get Started
          </Button>
        </div>
      </div>
    </div>
  );
};

export default LandingPage;
