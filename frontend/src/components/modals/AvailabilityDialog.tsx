import {
  Dialog,
  DialogTrigger,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
  DialogFooter,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { useState } from "react";
import { AvailabilitySlot } from "@/types/profile";

const daysOfWeek = [
  "Monday",
  "Tuesday",
  "Wednesday",
  "Thursday",
  "Friday",
  "Saturday",
  "Sunday",
];

export function AvailabilityDialog({
  initialAvailability = [],
  onSave,
}: {
  initialAvailability?: AvailabilitySlot[];
  onSave: (updated: AvailabilitySlot[]) => void;
}) {
  const [availability, setAvailability] = useState<
    Record<string, { from: string; to: string }>
  >(() => {
    const base: Record<string, { from: string; to: string }> = {};
    daysOfWeek.forEach((day) => {
      const match = initialAvailability.find((d) => d.day === day);
      base[day] = { from: match?.from || "", to: match?.to || "" };
    });
    return base;
  });

  const handleChange = (day: string, key: "from" | "to", value: string) => {
    setAvailability((prev) => ({
      ...prev,
      [day]: {
        ...prev[day],
        [key]: value,
      },
    }));
  };

  const handleSave = () => {
    const result: AvailabilitySlot[] = daysOfWeek
      .filter((day) => availability[day].from && availability[day].to)
      .map((day) => ({
        day,
        from: availability[day].from,
        to: availability[day].to,
      }));
    onSave(result);
  };

  return (
    <Dialog>
      <DialogTrigger asChild>
        <Button variant="outline" size="sm">
          Edit
        </Button>
      </DialogTrigger>
      <DialogContent className="max-w-2xl">
        <DialogHeader>
          <DialogTitle>Edit Availability Slots</DialogTitle>
          <DialogDescription>
            Provide your availability for each day. Only one time slot per day
            is allowed.
          </DialogDescription>
        </DialogHeader>

        <div className="grid gap-4 max-h-[60vh] overflow-auto">
          {daysOfWeek.map((day) => (
            <div key={day} className="grid grid-cols-3 items-center gap-2">
              <Label>{day}</Label>
              <Input
                placeholder="From (e.g. 18:00)"
                value={availability[day].from}
                onChange={(e) => handleChange(day, "from", e.target.value)}
              />
              <Input
                placeholder="To (e.g. 22:00)"
                value={availability[day].to}
                onChange={(e) => handleChange(day, "to", e.target.value)}
              />
            </div>
          ))}
        </div>

        <DialogFooter>
          <Button onClick={handleSave}>Save</Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
