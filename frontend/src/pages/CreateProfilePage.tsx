import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useFetchWithAuth } from "../lib/utils";
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

export default function CreateProfile() {
  const [nickname, setNickname] = useState("");
  const [debouncedNickname, setDebouncedNickname] = useState(nickname);
  const [bio, setBio] = useState("");
  const [location, setLocation] = useState("");
  const [avatarFile, setAvatarFile] = useState<File | null>(null);
  const [available, setAvailable] = useState<boolean | null>(null);
  const [checking, setChecking] = useState(false);
  const [loading, setLoading] = useState(false);

  type Country = {
    name: string;
    flag: string;
  };

  const [countries, setCountries] = useState<Country[]>([]);

  const fetchWithAuth = useFetchWithAuth();
  const navigate = useNavigate();

  useEffect(() => {
    fetch("https://restcountries.com/v3.1/all")
      .then((res) => res.json())
      .then((data) => {
        const countryList: Country[] = data
          // eslint-disable-next-line @typescript-eslint/no-explicit-any
          .map((c: any) => ({
            name: c.name.common,
            flag: c.flags.svg || c.flags.png || "",
          }))
          .sort((a: Country, b: Country) => a.name.localeCompare(b.name));
        setCountries(countryList);
      })
      .catch((err) => console.error("Failed to fetch countries", err));
  }, []);

  useEffect(() => {
    const handler = setTimeout(() => {
      setDebouncedNickname(nickname);
    }, 400);

    return () => clearTimeout(handler);
  }, [nickname]);

  useEffect(() => {
    if (!debouncedNickname.trim()) return;

    const check = async () => {
      setChecking(true);
      try {
        const res = await fetchWithAuth(
          `/user/profile/check-nickname?nickname=${encodeURIComponent(
            debouncedNickname
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
    };

    check();
  }, [debouncedNickname]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (available === false) return alert("Nickname is already taken.");
    setLoading(true);

    try {
      await fetchWithAuth("/user/profile", {
        method: "POST",
        body: JSON.stringify({ nickname, bio, location }),
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
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-background px-4 py-10">
      <div className="shadow-xl rounded-xl bg-card text-card-foreground container max-w-2xl mx-auto">
        <Card className="rounded-xl w-full">
          <CardContent className="p-8 space-y-6">
            <h2 className="text-3xl font-bold text-center">
              Create Your Profile
            </h2>

            <form onSubmit={handleSubmit} className="space-y-6">
              {/* Avatar */}
              <div className="space-y-4">
                {/* File input */}
                <div className="space-y-2">
                  <Label htmlFor="avatar">Avatar</Label>
                  <div className="relative w-full">
                    <input
                      id="avatar"
                      type="file"
                      accept="image/*"
                      onChange={(e) =>
                        setAvatarFile(e.target.files?.[0] || null)
                      }
                      className="absolute inset-0 opacity-0 cursor-pointer z-10"
                    />
                    <div className="flex items-center justify-between px-4 py-2 border border-input rounded-md bg-muted text-sm">
                      <span className="truncate text-muted-foreground">
                        {avatarFile?.name ?? "No file selected"}
                      </span>
                      <span className="text-primary font-medium">Browse</span>
                    </div>
                  </div>
                </div>

                {/* Avatar preview */}
                {avatarFile && (
                  <div className="flex justify-center">
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
                  required
                  placeholder="GamerTag123"
                  value={nickname}
                  onChange={(e) => setNickname(e.target.value)}
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
              </div>

              {/* Bio */}
              <div className="space-y-2">
                <Label htmlFor="bio">Bio</Label>
                <Textarea
                  id="bio"
                  placeholder="Tell us something about yourself..."
                  rows={4}
                  value={bio}
                  onChange={(e) => setBio(e.target.value)}
                />
              </div>

              {/* Country Selector */}
              <div className="space-y-2">
                <Label htmlFor="location">Country</Label>
                <Select onValueChange={setLocation}>
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
              </div>

              {/* Submit */}
              <Button
                type="submit"
                className="w-full"
                disabled={loading || available === false}
              >
                {loading ? "Creating..." : "Create Profile"}
              </Button>
            </form>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
