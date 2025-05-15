import { Auth0Provider } from "@auth0/auth0-react";

const domain = import.meta.env.VITE_AUTH0_DOMAIN;
const clientId = import.meta.env.VITE_AUTH0_CLIENT_ID;

export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
  return (
    <Auth0Provider
      domain={domain}
      clientId={clientId}
      authorizationParams={{
        redirect_uri: `${window.location.origin}/auth/callback`,
        audience: "https://gamemate.fyi/api",
        scope: "openid profile email",
      }}
    >
      {children}
    </Auth0Provider>
  );
};
