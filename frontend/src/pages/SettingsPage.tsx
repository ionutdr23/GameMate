import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Button } from "@/components/ui/button";
import { Sun, Moon, Laptop } from "lucide-react";
import { useTheme } from "@/hooks/useTheme";

const SettingsPage = () => {
  const { theme, setTheme } = useTheme();

  return (
    <div className="max-w-xl mx-auto px-4 py-10 space-y-6">
      <h1 className="text-2xl font-semibold">Settings</h1>

      <div>
        <h2 className="text-lg font-medium mb-2">Theme</h2>
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button variant="outline" className="flex items-center gap-2">
              {theme === "light" && <Sun className="w-4 h-4" />}
              {theme === "dark" && <Moon className="w-4 h-4" />}
              {theme === "system" && <Laptop className="w-4 h-4" />}
              <span className="capitalize">{theme} theme</span>
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent
            align="start"
            className="bg-secondary-background"
          >
            <DropdownMenuItem onClick={() => setTheme("light")}>
              <Sun className="w-4 h-4 mr-2" />
              Light
            </DropdownMenuItem>
            <DropdownMenuItem onClick={() => setTheme("dark")}>
              <Moon className="w-4 h-4 mr-2" />
              Dark
            </DropdownMenuItem>
            <DropdownMenuItem onClick={() => setTheme("system")}>
              <Laptop className="w-4 h-4 mr-2" />
              System
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      </div>
    </div>
  );
};

export default SettingsPage;
