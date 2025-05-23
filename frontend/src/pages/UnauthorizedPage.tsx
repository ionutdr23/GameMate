import { Button } from "@/components/ui/button";
import { useNavigate } from "react-router-dom";
import { Lock } from "lucide-react";

export function UnauthorizedPage() {
  const navigate = useNavigate();

  return (
    <div className="min-h-screen flex flex-col items-center justify-center text-center p-6">
      <Lock className="w-16 h-16 mb-4 text-red-500" />
      <h1 className="text-3xl font-bold mb-2">Access Denied</h1>
      <p className="text-gray-500 mb-6">
        You don't have permission to view this page.
      </p>
      <Button onClick={() => navigate("/")} variant="default">
        Go to Home
      </Button>
    </div>
  );
}
