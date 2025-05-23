import { useEffect, useState } from "react";
import { useForm, Controller } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { useNavigate } from "react-router-dom";
import { CreateProfileFormValues, createProfileSchema } from "@/lib/schemas";
import { fetchCountryList, useFetchWithAuth } from "../lib/utils";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Label } from "@/components/ui/label";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import {
  Select,
  SelectTrigger,
  SelectValue,
  SelectContent,
  SelectItem,
} from "@/components/ui/select";

type Country = {
  name: string;
  flag: string;
};

export default function CreateProfile() {
  const navigate = useNavigate();

  const [countries, setCountries] = useState<Country[]>([]);

  useEffect(() => {
    const fetchCountries = async () => {
      setCountries(await fetchCountryList());
    };
    fetchCountries();
  }, []);

  const {
    register,
    handleSubmit,
    control,
    watch,
    formState: { errors, isSubmitting },
  } = useForm<CreateProfileFormValues>({
    resolver: zodResolver(createProfileSchema),
  });
  const nickname = watch("nickname");
  const [available, setAvailable] = useState<boolean | null>(null);
  const [checking, setChecking] = useState(false);
  const [avatarFile, setAvatarFile] = useState<File | null>(null);
  const fetchWithAuth = useFetchWithAuth();

  // Debounced nickname availability check
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

  // Submission
  const onSubmit = async (data: CreateProfileFormValues) => {
    if (available === false) {
      alert("Nickname is already taken.");
      return;
    }

    try {
      await fetchWithAuth("/user/profile", {
        method: "POST",
        body: JSON.stringify(data),
        headers: { "Content-Type": "application/json" },
      });

      if (avatarFile) {
        const formData = new FormData();
        formData.append("file", avatarFile);

        const res = await fetchWithAuth("/user/profile/avatar", {
          method: "POST",
          body: formData,
        });

        if (!res.ok) {
          console.warn("Avatar upload failed", await res.text());
        }
      }

      navigate("/profile", { replace: true });
    } catch (err) {
      console.error(err);
      alert("Profile creation failed");
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-background px-4 py-10">
      <Card className="rounded-2xl w-full shadow-xl text-card-foreground container max-w-2xl mx-auto">
        <CardContent className="p-8 space-y-6">
          <h2 className="text-3xl font-bold text-center">
            Create Your Profile
          </h2>

          <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
            {/* Avatar */}
            <div className="space-y-2">
              <Label htmlFor="avatar">Avatar</Label>
              <div className="relative w-full">
                <input
                  id="avatar"
                  type="file"
                  accept="image/*"
                  onChange={(e) => setAvatarFile(e.target.files?.[0] || null)}
                  className="absolute inset-0 opacity-0 cursor-pointer z-10"
                />
                <div className="flex items-center justify-between px-4 py-2 border border-input bg-input text-foreground rounded-md text-sm transition-colors">
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
            </div>

            {/* Nickname */}
            <div className="space-y-2">
              <Label htmlFor="nickname">Nickname *</Label>
              <Input
                id="nickname"
                placeholder="GamerTag123"
                {...register("nickname")}
              />
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

            {/* Bio */}
            <div className="space-y-2">
              <Label htmlFor="bio">Bio</Label>
              <Textarea id="bio" rows={4} {...register("bio")} />
              {errors.bio && (
                <p className="text-sm text-destructive">{errors.bio.message}</p>
              )}
            </div>

            {/* Country */}
            <div className="space-y-2 w-full">
              <Label htmlFor="location">Country</Label>
              <Controller
                name="location"
                control={control}
                render={({ field }) => (
                  <Select onValueChange={field.onChange} value={field.value}>
                    <SelectTrigger id="location">
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

            <Button
              type="submit"
              className="w-full"
              disabled={isSubmitting || available === false}
            >
              {isSubmitting ? "Creating..." : "Create Profile"}
            </Button>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
