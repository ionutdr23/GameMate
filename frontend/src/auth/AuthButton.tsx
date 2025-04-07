import { Button } from "../components/ui/button";
import { useAuth } from "./useAuth";

export const AuthButton = () => {
  const { loginWithRedirect, logout, isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return <Button disabled={true}>Loading...</Button>;
  }

  return (
    <div className="flex items-center gap-4">
      {!isAuthenticated ? (
        <Button onClick={() => loginWithRedirect()}>Login</Button>
      ) : (
        <>
          <Button
            className="bg-red-500 hover:bg-red-600"
            onClick={() =>
              logout({ logoutParams: { returnTo: window.location.origin } })
            }
          >
            Logout
          </Button>
        </>
      )}
    </div>
  );
};
