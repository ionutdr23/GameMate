import { z } from "zod";

export const createProfileSchema = z.object({
  nickname: z.string().min(3, "Nickname is required"),
  bio: z.string().optional(),
  location: z.string().min(1, "Select a country"),
});

export type CreateProfileFormValues = z.infer<typeof createProfileSchema>;
