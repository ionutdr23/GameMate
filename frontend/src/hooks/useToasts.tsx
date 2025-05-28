import { toast } from "sonner";
import { Button } from "@/components/ui/button";

export const useToasts = () => {
  const showSuccess = (message: string) => toast.success(message);
  const showError = (message: string) => toast.error(message);
  const showInfo = (message: string) => toast(message);

  const confirm = ({
    title,
    description,
    onConfirm,
  }: {
    title: string;
    description?: string;
    onConfirm: () => void | Promise<void>;
  }) => {
    toast.custom((t) => (
      <div className="flex flex-col gap-2 p-4 rounded-md bg-background shadow-lg border max-w-sm w-full">
        <div className="text-sm font-medium">{title}</div>
        {description && (
          <div className="text-xs text-muted-foreground">{description}</div>
        )}
        <div className="flex justify-end gap-2 pt-2">
          <Button variant="outline" size="sm" onClick={() => toast.dismiss(t)}>
            Cancel
          </Button>
          <Button
            variant="destructive"
            size="sm"
            onClick={async () => {
              await onConfirm();
              toast.dismiss(t);
            }}
          >
            Confirm
          </Button>
        </div>
      </div>
    ));
  };

  return { showSuccess, showError, showInfo, confirm };
};
