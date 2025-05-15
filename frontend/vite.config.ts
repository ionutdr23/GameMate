import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import tailwindcss from "@tailwindcss/vite";
import path from "path";

export default defineConfig(({ mode }) => {
  return {
    plugins: [react(), tailwindcss()],
    build: {
      outDir: mode === "staging" ? "./dist/staging/" : "./dist/production/",
    },
    resolve: {
      alias: {
        "@": path.resolve(__dirname, "./src"),
      },
    },
  };
});
