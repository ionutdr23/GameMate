import { Button } from "@/components/ui/button";
import { useAuth } from "./useAuth";

export const AuthButton = () => {
  const { loginWithRedirect, logout, isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return <Button disabled>Loading...</Button>;
  }

  return (
    <div className="flex items-center gap-4">
      {!isAuthenticated ? (
        <Button onClick={() => loginWithRedirect()}>Login</Button>
      ) : (
        <Button
          variant="destructive"
          onClick={() =>
            logout({ logoutParams: { returnTo: window.location.origin } })
          }
        >
          Logout
        </Button>
      )}
    </div>
  );
};
