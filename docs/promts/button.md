You are given a task to integrate an existing React component in the codebase

The codebase should support:
- shadcn project structure  
- Tailwind CSS
- Typescript

If it doesn't, provide instructions on how to setup project via shadcn CLI, install Tailwind or Typescript.

Determine the default path for components and styles. 
If default path for components is not /components/ui, provide instructions on why it's important to create this folder
Copy-paste this component to /components/ui folder:
```tsx
click-powerup.tsx
"use client";

import { cn } from "@/lib/utils";
import { motion } from "motion/react";
import { useState } from "react";

export const ClickPowerUp = ({
  children,
  className,
  tapDuration = 500,
}: {
  children: React.ReactNode;
  className?: string;
  tapDuration?: number;
}) => {
  const [isTapped, setIsTapped] = useState(false);

  const handleTap = () => {
    if (isTapped) return;
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
      className="relative inline-block cursor-pointer [--pattern:var(--color-neutral-200)] dark:[--pattern:var(--color-neutral-900)]"
    >
      {/* Corner brackets */}
      {[
        {
          corner: "top-right",
          cls: "absolute top-0 right-0 size-2 border-t border-r z-20",
        },
        {
          corner: "top-left",
          cls: "absolute top-0 left-0 size-2 border-t border-l z-20",
        },
        {
          corner: "bottom-left",
          cls: "absolute bottom-0 left-0 size-2 border-b border-l z-20",
        },
        {
          corner: "bottom-right",
          cls: "absolute right-0 bottom-0 size-2 border-r border-b z-20",
        },
      ].map(({ corner, cls }) => (
        <motion.div
          key={corner}
          custom={corner}
          variants={{
            rest: () => ({ x: 0, y: 0, borderColor: "rgb(38 38 38)" }),
            hover: (c: string) => ({
              x: c.includes("right") ? 3 : -3,
              y: c.includes("bottom") ? 3 : -3,
              borderColor: "rgb(38 38 38)",
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
        className={cn(
          "relative overflow-hidden px-10 py-3 font-medium uppercase",
          className,
        )}
      >
        {/* Pattern */}
        <span className="absolute inset-0 z-0 bg-[repeating-linear-gradient(315deg,var(--pattern)_0,var(--pattern)_1px,transparent_0,transparent_50%)] bg-size-[7px_7px]" />

        {/* Arm panel */}
        <motion.span
          variants={{
            rest: { scaleX: 0, originX: 0, backgroundColor: "#171717" },
            hover: { scaleX: 1, originX: 0, backgroundColor: "#171717" },
            tap: { scaleX: 1, originX: 0, backgroundColor: "#2CD4BD" },
          }}
          transition={{ type: "spring", stiffness: 220, damping: 22 }}
          className="absolute inset-0 z-10 origin-left"
        />

        {/* Text */}
        <motion.span
          variants={{
            rest: { color: "var(--color-foreground)" },
            hover: { color: "#ffffff" },
            tap: { color: "#0a2926" },
          }}
          transition={{ type: "spring", stiffness: 220, damping: 22 }}
          className="relative z-20"
        >
          {children}
        </motion.span>
      </button>
    </motion.div>
  );
};

export default ClickPowerUp;


demo.tsx
"use client";

import { ClickPowerUp } from "@/components/ui/click-powerup";

export default function Default() {
  return (
    <div className="flex min-h-screen w-full items-center justify-center bg-background p-8">
      <ClickPowerUp>Deploy Doom</ClickPowerUp>
    </div>
  );
}

```

Install NPM dependencies:
```bash
motion
```

Implementation Guidelines
 1. Analyze the component structure and identify all required dependencies
 2. Review the component's argumens and state
 3. Identify any required context providers or hooks and install them
 4. Questions to Ask
 - What data/props will be passed to this component?
 - Are there any specific state management requirements?
 - Are there any required assets (images, icons, etc.)?
 - What is the expected responsive behavior?
 - What is the best place to use this component in the app?

Steps to integrate
 0. Copy paste all the code above in the correct directories
 1. Install external dependencies
 2. Fill image assets with Unsplash stock images you know exist
 3. Use lucide-react icons for svgs or logos if component requires them
