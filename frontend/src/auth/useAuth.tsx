import { useAuth0 } from "@auth0/auth0-react";

const ROLE_CLAIM = "https://gamemate.fyi/claims/roles";

export const useAuth = () => {
  const {
    user,
    isAuthenticated,
    isLoading,
    error,
    loginWithRedirect,
    logout,
    getAccessTokenSilently,
  } = useAuth0();

  const roles: string[] = user?.[ROLE_CLAIM] || [];

  const hasRole = (role: string) => roles.includes(role);

  const hasAnyRole = (required: string[]) =>
    required.some((role) => roles.includes(role));

  return {
    user,
    isAuthenticated,
    isLoading,
    error,
    loginWithRedirect,
    logout,
    getAccessTokenSilently,
    roles,
    hasRole,
    hasAnyRole,
  };
};
