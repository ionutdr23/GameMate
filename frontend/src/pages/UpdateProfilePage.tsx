"use client";

import { useEffect, useState } from "react";
import { useForm, Controller } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { profileSchema } from "@/validation/profile";

import { useProfile } from "@/hooks/useProfile";
import { Card, CardContent } from "@/components/ui/card";
import { Label } from "@radix-ui/react-label";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { fetchCountryList, useFetchWithAuth } from "@/lib/utils";

type ProfileFormData = z.infer<typeof profileSchema>;

export default function UpdateProfilePage() {
  const fetchWithAuth = useFetchWithAuth();
  const { uploadAvatar, updateProfile } = useProfile();
  const [avatarFile, setAvatarFile] = useState<File | null>(null);
  const [countries, setCountries] = useState<{ name: string; flag: string }[]>(
    []
  );
  const [available, setAvailable] = useState<boolean | null>(null);
  const [checking, setChecking] = useState(false);
  const [success, setSuccess] = useState(false);

  const {
    register,
    handleSubmit,
    control,
    watch,
    formState: { errors, isSubmitting },
  } = useForm<ProfileFormData>({
    resolver: zodResolver(profileSchema),
  });

  const nickname = watch("nickname");

  useEffect(() => {
    const fetchCountries = async () => {
      setCountries(await fetchCountryList());
    };
    fetchCountries();
  }, []);

  useEffect(() => {
    if (!nickname || nickname.trim().length < 3) {
      setAvailable(null);
      return;
    }

    const delay = setTimeout(async () => {
      setChecking(true);
      try {
        const res = await fetchWithAuth(
          `/user/profile/check-nickname?nickname=${encodeURIComponent(
            nickname
          )}`
        );
        const data = await res.json();
        setAvailable(data.available);
      } catch (err) {
        console.error("Nickname check failed", err);
        setAvailable(null);
      } finally {
        setChecking(false);
      }
    }, 400);

    return () => clearTimeout(delay);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [nickname]);

  const handleAvatarUpload = async () => {
    if (!avatarFile) return;
    try {
      await uploadAvatar(avatarFile);
    } catch (err) {
      console.error("Avatar upload failed:", err);
    }
  };

  const onSubmit = async (data: ProfileFormData) => {
    try {
      await updateProfile({
        nickname: data.nickname,
        bio: data.bio || undefined,
        location: data.location || undefined,
      });

      setSuccess(true);
      setTimeout(() => setSuccess(false), 3000);
    } catch (err) {
      console.error("Profile update failed:", err);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-background px-4 py-10">
      <Card className="rounded-2xl w-full shadow-xl text-card-foreground container max-w-2xl mx-auto">
        <CardContent className="p-8 space-y-6">
          <h2 className="text-3xl font-bold text-center">
            Update Your Profile
          </h2>

          {/* Avatar Upload */}
          <div className="space-y-4">
            <Label htmlFor="avatar">Avatar</Label>
            <div className="relative w-full">
              <input
                id="avatar"
                type="file"
                accept="image/*"
                onChange={(e) => setAvatarFile(e.target.files?.[0] || null)}
                className="absolute inset-0 opacity-0 cursor-pointer z-10"
              />
              <div className="flex items-center justify-between px-4 py-2 border border-input bg-input text-foreground rounded-md text-sm">
                <span className="truncate text-muted-foreground">
                  {avatarFile?.name ?? "No file selected"}
                </span>
                <span className="text-primary font-medium">Browse</span>
              </div>
            </div>
            {avatarFile && (
              <div className="flex justify-center pt-2">
                <img
                  src={URL.createObjectURL(avatarFile)}
                  alt="Selected avatar"
                  className="w-24 h-24 rounded-full border border-border object-cover shadow-md"
                />
              </div>
            )}
            <Button
              onClick={handleAvatarUpload}
              disabled={!avatarFile}
              className="w-full"
            >
              Upload Avatar
            </Button>
          </div>

          {/* Profile Form */}
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-6 pt-6">
            <div className="space-y-2">
              <Label htmlFor="nickname">Nickname *</Label>
              <Input id="nickname" {...register("nickname")} />
              {checking && (
                <p className="text-sm text-muted-foreground">
                  Checking availability...
                </p>
              )}
              {available === true && (
                <p className="text-sm text-green-600">
                  ✓ Nickname is available
                </p>
              )}
              {available === false && (
                <p className="text-sm text-destructive">
                  ✗ Nickname already taken
                </p>
              )}
              {errors.nickname && (
                <p className="text-sm text-destructive">
                  {errors.nickname.message}
                </p>
              )}
            </div>

            <div className="space-y-2">
              <Label htmlFor="bio">Bio</Label>
              <Textarea id="bio" rows={4} {...register("bio")} />
              {errors.bio && (
                <p className="text-sm text-destructive">{errors.bio.message}</p>
              )}
            </div>

            <div className="space-y-2">
              <Label htmlFor="location">Country</Label>
              <Controller
                name="location"
                control={control}
                render={({ field }) => (
                  <Select onValueChange={field.onChange} value={field.value}>
                    <SelectTrigger id="location" className="w-full">
                      <SelectValue placeholder="Select your country" />
                    </SelectTrigger>
                    <SelectContent>
                      {countries.map((country) => (
                        <SelectItem key={country.name} value={country.name}>
                          <div className="flex items-center gap-2">
                            <img
                              src={country.flag}
                              alt={`${country.name} flag`}
                              className="w-5 h-4 rounded-sm object-cover"
                            />
                            <span>{country.name}</span>
                          </div>
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                )}
              />
              {errors.location && (
                <p className="text-sm text-destructive">
                  {errors.location.message}
                </p>
              )}
            </div>
            {success && (
              <p className="text-center text-green-600 font-medium">
                ✓ Profile updated successfully
              </p>
            )}
            <Button
              type="submit"
              className="w-full"
              disabled={isSubmitting || available === false}
            >
              {isSubmitting ? "Saving..." : "Save Changes"}
            </Button>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
