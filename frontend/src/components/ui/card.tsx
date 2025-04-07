import { cn } from "../../lib/utils";

export const Card: React.FC<React.HTMLAttributes<HTMLDivElement>> = ({
  className,
  children,
  ...props
}) => {
  return (
    <div
      className={cn(
        "rounded-2xl shadow-lg border border-gray-700 bg-gray-900",
        className
      )}
      {...props}
    >
      {children}
    </div>
  );
};

export const CardContent: React.FC<React.HTMLAttributes<HTMLDivElement>> = ({
  className,
  children,
  ...props
}) => {
  return (
    <div className={cn("p-4", className)} {...props}>
      {children}
    </div>
  );
};
