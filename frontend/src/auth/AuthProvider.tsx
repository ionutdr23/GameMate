import { Auth0Provider, AppState } from "@auth0/auth0-react";
import { useNavigate } from "react-router-dom";

const domain = import.meta.env.VITE_AUTH0_DOMAIN;
const clientId = import.meta.env.VITE_AUTH0_CLIENT_ID;

export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
  const navigate = useNavigate();

  const onRedirectCallback = (appState?: AppState) => {
    navigate(appState?.returnTo || "/profile");
  };

  return (
    <Auth0Provider
      domain={domain}
      clientId={clientId}
      authorizationParams={{
        redirect_uri: `${window.location.origin}/auth/callback`,
      }}
      onRedirectCallback={onRedirectCallback}
      useRefreshTokens
      cacheLocation="localstorage"
    >
      {children}
    </Auth0Provider>
  );
};
