import { Button } from "@/components/ui/button";
import { useNavigate } from "react-router-dom";
import { SearchX } from "lucide-react";

export function NotFoundPage() {
  const navigate = useNavigate();

  return (
    <div className="min-h-screen flex flex-col items-center justify-center text-center p-6">
      <SearchX className="w-16 h-16 mb-4 text-yellow-500" />
      <h1 className="text-3xl font-bold mb-2">Page Not Found</h1>
      <p className="text-gray-500 mb-6">
        The page you are looking for doesn’t exist or has been moved.
      </p>
      <Button onClick={() => navigate("/")} variant="default">
        Back to Home
      </Button>
    </div>
  );
}
