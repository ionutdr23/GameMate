import { z } from "zod";

export const profileSchema = z.object({
  nickname: z
    .string()
    .min(3, "Nickname must be at least 3 characters long")
    .max(20, "Nickname must be at most 20 characters long"),
  bio: z
    .string()
    .max(250, "Bio must be under 250 characters")
    .optional()
    .or(z.literal("")),
  location: z.string().optional().or(z.literal("")),
});
