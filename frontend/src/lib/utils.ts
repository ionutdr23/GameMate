import { useAuth0 } from "@auth0/auth0-react";
import { clsx, type ClassValue } from "clsx";
import { twMerge } from "tailwind-merge";
import { useMemo } from "react";
import axios, { InternalAxiosRequestConfig, AxiosInstance } from "axios";

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

export function useAxiosWithAuth(): AxiosInstance {
  const { getAccessTokenSilently } = useAuth0();

  const instance = useMemo(() => {
    const domain =
      window.location.hostname === "localhost"
        ? "gamemate.fyi"
        : window.location.hostname;
    const baseURL = `https://api.${domain}/api`;

    const axiosInstance = axios.create({ baseURL });

    axiosInstance.interceptors.request.use(
      async (config: InternalAxiosRequestConfig) => {
        const token = await getAccessTokenSilently();
        config.headers = config.headers ?? {};
        (config.headers as Record<string, string>)[
          "Authorization"
        ] = `Bearer ${token}`;
        return config;
      }
    );

    return axiosInstance;
  }, [getAccessTokenSilently]);

  return instance;
}

export async function fetchCountryList() {
  const res = await axios.get("https://restcountries.com/v3.1/all");
  if (res.status !== 200) {
    throw new Error("Failed to fetch countries");
  }
  const data = res.data as Array<{
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
