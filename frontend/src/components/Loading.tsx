import { Loader2 } from "lucide-react";
import { Card, CardContent } from "@/components/ui/card";

export default function Loading() {
  return (
    <div className="flex h-screen items-center justify-center bg-background">
      <Card className="p-6 shadow-xl animate-in fade-in duration-500">
        <CardContent className="flex flex-col items-center justify-center gap-4">
          <Loader2 className="h-10 w-10 animate-spin text-primary" />
          <p className="text-sm text-muted-foreground">
            Loading, please wait...
          </p>
        </CardContent>
      </Card>
    </div>
  );
}
