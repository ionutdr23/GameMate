import { useAuth0 } from "@auth0/auth0-react";

// This function is used to fetch data from a given URL with an authorization token.
// It uses the Auth0 library to get the token silently and adds it to the request headers.
export function useFetchWithAuth() {
  const { getAccessTokenSilently } = useAuth0();

  return async function fetchWithAuth(
    url: string,
    options: RequestInit = {}
  ): Promise<Response> {
    const token = await getAccessTokenSilently();

    const headers = new Headers(options.headers || {});
    headers.append("Authorization", `Bearer ${token}`);

    return fetch(url, { ...options, headers });
  };
}

// This function is used to combine multiple class names into a single string, filtering out any falsy values (like undefined, null, or false).
export function cn(
  ...classes: Array<string | undefined | null | false>
): string {
  return classes.filter(Boolean).join(" ");
}
