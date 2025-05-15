import { useNavigate } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/auth/useAuth";
import { AuthButton } from "@/auth/AuthButton";

const LandingPage = () => {
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();

  return (
    <div className="min-h-screen flex items-center justify-center bg-background text-foreground px-4">
      <div className="text-center space-y-6 max-w-xl">
        <h1 className="text-5xl font-bold">Welcome to GameMate</h1>
        <p className="text-xl text-muted-foreground">
          Find teammates and connect with gamers worldwide.
        </p>

        <div className="flex justify-center">
          {isAuthenticated ? (
            <Button variant="default" onClick={() => navigate("/feed")}>
              Go to Feed
            </Button>
          ) : (
            <AuthButton />
          )}
        </div>
      </div>
    </div>
  );
};

export default LandingPage;
