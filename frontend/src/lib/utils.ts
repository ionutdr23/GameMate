import { useAuth0 } from "@auth0/auth0-react";
import { clsx, type ClassValue } from "clsx";
import { useCallback } from "react";
import { twMerge } from "tailwind-merge";

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

export function useFetchWithAuth() {
  const { getAccessTokenSilently } = useAuth0();

  const fetchWithAuth = useCallback(
    async (path: string, options: RequestInit = {}): Promise<Response> => {
      const token = await getAccessTokenSilently();
      const headers = new Headers(options.headers || {});
      headers.append("Authorization", `Bearer ${token}`);

      const baseUrl = import.meta.env.VITE_API_URL?.replace(/\/+$/, "") ?? "";
      const fullUrl = `${baseUrl}${path.startsWith("/") ? path : `/${path}`}`;

      return fetch(fullUrl, { ...options, headers });
    },
    [getAccessTokenSilently]
  );

  return fetchWithAuth;
}

export async function fetchCountryList() {
  const res = await fetch("https://restcountries.com/v3.1/all");
  if (!res.ok) {
    throw new Error("Failed to fetch countries");
  }
  const data = (await res.json()) as Array<{
    name: { common: string };
    flags: { svg?: string; png?: string };
  }>;
  return data
    .map((c) => ({
      name: c.name.common,
      flag: c.flags.svg || c.flags.png || "",
    }))
    .sort((a, b) => a.name.localeCompare(b.name));
}
