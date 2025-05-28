import { useEffect, useMemo, useState } from "react";
import { useForm, Controller, useWatch } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { useDebounce } from "use-debounce";
import { useQuery } from "@tanstack/react-query";
import { profileSchema } from "@/validation/profile";
import { useProfile } from "@/hooks/useProfile";
import { fetchCountryList } from "@/lib/utils";
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

type ProfileFormData = z.infer<typeof profileSchema>;

export default function UpdateProfilePage() {
  const { uploadAvatar, updateProfile, checkNicknameAvailability } =
    useProfile();

  const [avatarFile, setAvatarFile] = useState<File | null>(null);
  const [success, setSuccess] = useState(false);

  const {
    register,
    handleSubmit,
    control,
    setError,
    clearErrors,
    formState: { errors, isSubmitting },
  } = useForm<ProfileFormData>({
    resolver: zodResolver(profileSchema),
    defaultValues: {
      nickname: "",
      bio: "",
      location: "",
    },
  });

  const nickname = useWatch({ control, name: "nickname" });
  const [debouncedNickname] = useDebounce(nickname, 200);

  const { data: countries = [], isLoading: loadingCountries } = useQuery({
    queryKey: ["countries"],
    queryFn: fetchCountryList,
    staleTime: 1000 * 60 * 60,
  });

  const [checking, setChecking] = useState(false);
  const [nicknameError, setNicknameError] = useState<string | null>(null);
  const [nicknameAvailable, setNicknameAvailable] = useState<boolean | null>(
    null
  );

  useEffect(() => {
    let cancelled = false;

    const checkNickname = async () => {
      if (debouncedNickname.trim().length < 3) {
        clearErrors("nickname");
        setNicknameAvailable(null);
        setNicknameError(null);
        return;
      }

      setChecking(true);
      setNicknameError(null);
      setNicknameAvailable(null);

      try {
        const available = await checkNicknameAvailability(debouncedNickname);
        if (cancelled) return;

        setNicknameAvailable(available);
        if (!available) {
          setError("nickname", {
            type: "manual",
            message: "Nickname already taken",
          });
        } else {
          clearErrors("nickname");
        }
      } catch {
        if (!cancelled) {
          setNicknameError("Could not check nickname");
          setError("nickname", {
            type: "manual",
            message: "Could not check nickname",
          });
        }
      } finally {
        if (!cancelled) {
          setChecking(false);
        }
      }
    };

    checkNickname();
    return () => {
      cancelled = true;
    };
  }, [debouncedNickname, checkNicknameAvailability, setError, clearErrors]);

  const avatarPreviewUrl = useMemo(() => {
    return avatarFile ? URL.createObjectURL(avatarFile) : null;
  }, [avatarFile]);

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
        bio: data.bio || "",
        location: data.location || "",
      });

      setSuccess(true);
      setTimeout(() => setSuccess(false), 3000);
    } catch (err) {
      console.error("Profile update failed:", err);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-background px-4 py-10">
      <Card className="rounded-2xl w-full shadow-xl max-w-2xl">
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
            {avatarPreviewUrl && (
              <div className="flex justify-center pt-2">
                <img
                  src={avatarPreviewUrl}
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
              {nicknameAvailable === true && !errors.nickname && (
                <p className="text-sm text-green-600">
                  ✓ Nickname is available
                </p>
              )}
              {errors.nickname && (
                <p className="text-sm text-destructive">
                  {errors.nickname.message}
                </p>
              )}
              {nicknameError && (
                <p className="text-sm text-destructive">{nicknameError}</p>
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
                      <SelectValue
                        placeholder="Select your country"
                        translate="no"
                      />
                    </SelectTrigger>
                    <SelectContent>
                      {loadingCountries ? (
                        <SelectItem value="-" disabled>
                          Loading...
                        </SelectItem>
                      ) : (
                        countries.map((country) => (
                          <SelectItem
                            key={country.name}
                            value={country.name}
                            translate="no"
                          >
                            <div className="flex items-center gap-2">
                              <img
                                src={country.flag}
                                alt={`${country.name} flag`}
                                className="w-5 h-4 rounded-sm object-cover"
                                loading="lazy"
                              />
                              <span>{country.name}</span>
                            </div>
                          </SelectItem>
                        ))
                      )}
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
            <Button type="submit" className="w-full" disabled={isSubmitting}>
              {isSubmitting ? "Saving..." : "Save Changes"}
            </Button>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
