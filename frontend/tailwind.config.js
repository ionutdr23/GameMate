/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./index.html",
    "./src/**/*.{ts,tsx}",
    "./components/**/*.{ts,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        background: "hsl(var(--background))",
        "secondary-background": "hsl(var(--secondary-background))",
      },
    },
  },
  plugins: [require("tailwindcss-animate")],
};
