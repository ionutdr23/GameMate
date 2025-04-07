import { cn } from "../../lib/utils";

interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: "default" | "outline" | "ghost";
}

export const Button: React.FC<ButtonProps> = ({
  className,
  variant = "default",
  children,
  ...props
}) => {
  const base =
    "inline-flex items-center justify-center font-medium rounded-2xl transition-colors focus:outline-none focus:ring-2 focus:ring-offset-2";
  const variants = {
    default: "bg-indigo-600 hover:bg-indigo-700 text-white",
    outline: "border border-gray-300 text-white hover:bg-gray-700",
    ghost: "text-white hover:bg-gray-800",
  };

  return (
    <button
      className={cn(base, variants[variant], "px-4 py-2", className)}
      {...props}
    >
      {children}
    </button>
  );
};
