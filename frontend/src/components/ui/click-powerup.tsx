"use client";

import { cn } from "@/lib/utils";
import { motion } from "motion/react";
import { ButtonHTMLAttributes, useState } from "react";

type PowerUpVariant = "primary" | "secondary";
type PowerUpSize = "sm" | "md" | "lg";

interface ClickPowerUpProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: PowerUpVariant;
  size?: PowerUpSize;
  tapDuration?: number;
}

const variantClasses: Record<PowerUpVariant, { corner: string; pattern: string; arm: string; textRest: string; textHover: string; textTap: string }> = {
  primary: {
    corner: "rgb(38 38 38)",
    pattern: "var(--color-neutral-200)",
    arm: "#171717",
    textRest: "var(--color-foreground)",
    textHover: "#ffffff",
    textTap: "#0a2926",
  },
  secondary: {
    corner: "rgb(38 38 38)",
    pattern: "var(--color-neutral-200)",
    arm: "#171717",
    textRest: "var(--color-foreground)",
    textHover: "#ffffff",
    textTap: "#0a2926",
  },
};

const sizeClasses: Record<PowerUpSize, string> = {
  sm: "px-6 py-2 text-sm",
  md: "px-10 py-3 font-medium",
  lg: "px-14 py-4 text-lg",
};

export function ClickPowerUp({
  children,
  className,
  variant = "primary",
  size = "md",
  tapDuration = 500,
  disabled,
  ...props
}: ClickPowerUpProps) {
  const [isTapped, setIsTapped] = useState(false);
  const colors = variantClasses[variant];

  const handleTap = () => {
    if (isTapped || disabled) return;
    setIsTapped(true);
    setTimeout(() => setIsTapped(false), tapDuration);
  };

  const state = isTapped ? "tap" : "rest";

  return (
    <motion.div
      initial="rest"
      animate={state}
      whileHover={isTapped ? "tap" : "hover"}
      onTap={handleTap}
      className={cn(
        "relative inline-block cursor-pointer",
        "[--pattern:var(--color-neutral-200)] dark:[--pattern:var(--color-neutral-900)]",
        disabled && "opacity-50 cursor-not-allowed pointer-events-none",
      )}
    >
      {[
        { corner: "top-right", cls: "absolute top-0 right-0 size-2 border-t border-r z-20" },
        { corner: "top-left", cls: "absolute top-0 left-0 size-2 border-t border-l z-20" },
        { corner: "bottom-left", cls: "absolute bottom-0 left-0 size-2 border-b border-l z-20" },
        { corner: "bottom-right", cls: "absolute right-0 bottom-0 size-2 border-r border-b z-20" },
      ].map(({ corner, cls }) => (
        <motion.div
          key={corner}
          custom={corner}
          variants={{
            rest: () => ({ x: 0, y: 0, borderColor: colors.corner }),
            hover: (c: string) => ({
              x: c.includes("right") ? 3 : -3,
              y: c.includes("bottom") ? 3 : -3,
              borderColor: colors.corner,
            }),
            tap: (c: string) => ({
              x: c.includes("right") ? -2 : 2,
              y: c.includes("bottom") ? -2 : 2,
              borderColor: "#2CD4BD",
            }),
          }}
          transition={{ type: "spring", stiffness: 300, damping: 20 }}
          className={cls}
        />
      ))}

        <button
          disabled={disabled}
          className={cn(
            "relative overflow-hidden uppercase rounded-full",
            sizeClasses[size],
            className,
          )}
        {...props}
      >
        <span className="absolute inset-0 z-0 bg-[repeating-linear-gradient(315deg,var(--pattern)_0,var(--pattern)_1px,transparent_0,transparent_50%)] bg-size-[7px_7px]" />

        <motion.span
          variants={{
            rest: { scaleX: 0, originX: 0, backgroundColor: colors.arm },
            hover: { scaleX: 1, originX: 0, backgroundColor: colors.arm },
            tap: { scaleX: 1, originX: 0, backgroundColor: "#2CD4BD" },
          }}
          transition={{ type: "spring", stiffness: 220, damping: 22 }}
          className="absolute inset-0 z-10 origin-left"
        />

        <motion.span
          variants={{
            rest: { color: colors.textRest },
            hover: { color: colors.textHover },
            tap: { color: colors.textTap },
          }}
          transition={{ type: "spring", stiffness: 220, damping: 22 }}
          className="relative z-20"
        >
          {children}
        </motion.span>
      </button>
    </motion.div>
  );
}

export default ClickPowerUp;
