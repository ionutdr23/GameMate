import { useEffect, useState, useMemo } from "react";
import { useForm, Controller, useWatch } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { useNavigate } from "react-router-dom";
import { ProfileFormValues, profileSchema } from "@/validation/profile";
import { fetchCountryList } from "@/lib/utils";
import { useProfile } from "@/hooks/useProfile";
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
  const { checkNicknameAvailability, createProfile } = useProfile();

  const [countries, setCountries] = useState<Country[]>([]);
  const [avatarFile, setAvatarFile] = useState<File | null>(null);
  const [available, setAvailable] = useState<boolean | null>(null);
  const [checking, setChecking] = useState(false);
  const [nicknameError, setNicknameError] = useState<string | null>(null);

  const {
    register,
    handleSubmit,
    control,
    setError,
    clearErrors,
    formState: { errors, isSubmitting },
  } = useForm<ProfileFormValues>({
    resolver: zodResolver(profileSchema),
  });

  const nickname = useWatch({ control, name: "nickname" });

  useEffect(() => {
    const fetchCountriesAsync = async () => {
      const data = await fetchCountryList();
      setCountries(data);
    };
    fetchCountriesAsync();
  }, []);

  // Debounced nickname check
  useEffect(() => {
    if (!nickname || nickname.trim().length < 3) {
      clearErrors("nickname");
      setAvailable(null);
      setNicknameError(null);
      return;
    }

    let cancelled = false;

    const check = async () => {
      setChecking(true);
      setAvailable(null);
      setNicknameError(null);

      try {
        const isAvailable = await checkNicknameAvailability(nickname);
        if (cancelled) return;

        setAvailable(isAvailable);
        if (!isAvailable) {
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
        if (!cancelled) setChecking(false);
      }
    };

    const delay = setTimeout(check, 400);
    return () => {
      cancelled = true;
      clearTimeout(delay);
    };
  }, [nickname, checkNicknameAvailability, setError, clearErrors]);

  const avatarPreviewUrl = useMemo(() => {
    return avatarFile ? URL.createObjectURL(avatarFile) : null;
  }, [avatarFile]);

  const onSubmit = async (data: ProfileFormValues) => {
    if (available === false) {
      alert("Nickname is already taken.");
      return;
    }

    try {
      await createProfile(data, avatarFile ?? undefined);
      navigate("/profile", { replace: true });
    } catch (err) {
      console.error(err);
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
            {/* Avatar Upload */}
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
            </div>

            {/* Nickname */}
            <div className="space-y-2">
              <Label htmlFor="nickname">Nickname *</Label>
              <Input
                id="nickname"
                placeholder="GamerTag123"
                className="bg-background"
                {...register("nickname")}
              />
              {checking && (
                <p className="text-sm text-muted-foreground">
                  Checking availability...
                </p>
              )}
              {available === true && !errors.nickname && (
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

            {/* Bio */}
            <div className="space-y-2">
              <Label htmlFor="bio">Bio</Label>
              <Textarea
                id="bio"
                rows={4}
                {...register("bio")}
                className="bg-background"
              />
              {errors.bio && (
                <p className="text-sm text-destructive">{errors.bio.message}</p>
              )}
            </div>

            {/* Country */}
            <div className="space-y-2">
              <Label htmlFor="location">Country</Label>
              <Controller
                name="location"
                control={control}
                render={({ field }) => (
                  <Select onValueChange={field.onChange} value={field.value}>
                    <SelectTrigger
                      id="location"
                      className="bg-background w-full"
                    >
                      <SelectValue
                        placeholder="Select your country"
                        translate="no"
                      />
                    </SelectTrigger>
                    <SelectContent>
                      {countries.map((country) => (
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
