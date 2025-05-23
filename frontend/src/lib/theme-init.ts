const stored = localStorage.getItem("theme") as
  | "light"
  | "dark"
  | "system"
  | null;
const prefersDark = window.matchMedia("(prefers-color-scheme: dark)").matches;
const isDark = stored === "dark" || (stored === "system" && prefersDark);
document.documentElement.classList.toggle("dark", isDark);
