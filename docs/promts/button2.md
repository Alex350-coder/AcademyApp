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
delete-button.tsx
"use client";

import { Undo03Icon } from "@hugeicons/core-free-icons";
import { HugeiconsIcon } from "@hugeicons/react";
import { motion, AnimatePresence } from "motion/react";
import React, { useEffect, useState } from "react";

const DeleteButton = () => {
  const [isDeleting, setIsDeleting] = useState(false);
  const [count, setCount] = useState(10);
  const [isAnimating, setIsAnimating] = useState(false);

  useEffect(() => {
    if (!isDeleting) return;

    if (count === 0) return;

    const timer = setTimeout(() => setCount((c) => c - 1), 1000);
    return () => clearTimeout(timer);
  }, [isDeleting, count]);

  const handleClick = (newState: boolean) => {
    if (isAnimating) return;
    setIsAnimating(true);
    setIsDeleting(newState);
    if (newState) setCount(10);

    // Release lock after animation completes
    setTimeout(() => setIsAnimating(false), 400);
  };

  // Change Here
  const deleteText = "Delete Account";
  const cancelText = "Cancel Deletion";

  return (
    <div className="flex items-center justify-center">
      <AnimatePresence mode="popLayout" initial={false}>
        {!isDeleting ? (
          // STATE A
          <motion.button
            key="delete"
            layoutId="deleteButton"
            onClick={() => handleClick(true)}
            whileTap={{ scale: 0.95 }}
            style={{ pointerEvents: isAnimating ? "none" : "auto" }}
            initial={{
              backgroundColor: "#FFEDF1",
              filter: "blur(1px)",
              opacity: 1,
            }}
            animate={{
              backgroundColor: "#FE322A",
              filter: "blur(0px)",
              opacity: 1,
            }}
            exit={{
              backgroundColor: "#FFEDF1",
              filter: "blur(1px)",
              opacity: 0,
            }}
            className="text-white px-5 py-3 rounded-full flex items-center justify-center overflow-hidden"
            transition={{
              layout: { duration: 0.4, ease: [0.77, 0, 0.175, 1] },
              backgroundColor: { duration: 0.4, ease: "easeInOut" },
              filter: { duration: 0.1, ease: "easeInOut" },
              opacity: { duration: 0.2, ease: "easeOut" },
            }}
          >
            <motion.span
              layoutId="buttonText"
              className="flex"
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              transition={{ duration: 0.1 }}
            >
              {deleteText.split("").map((char, i) => (
                <motion.span
                  key={`delete-${i}`}
                  initial={{ y: 20, opacity: 0, scale: 0.3 }}
                  animate={{ y: 0, opacity: 1, scale: 1 }}
                  exit={{ y: -20, opacity: 0, scale: 0.3 }}
                  transition={{
                    duration: 0.3,
                    delay: i * 0.005,
                    ease: [0.785, 0.135, 0.15, 0.86],
                  }}
                  style={{ display: "inline-block", whiteSpace: "pre" }}
                >
                  {char}
                </motion.span>
              ))}
            </motion.span>
          </motion.button>
        ) : (
          // STATE B
          <motion.button
            key="cancel"
            layoutId="deleteButton"
            onClick={() => handleClick(false)}
            whileTap={{ scale: 0.95 }}
            style={{ pointerEvents: isAnimating ? "none" : "auto" }}
            initial={{
              backgroundColor: "#FE322A",
              filter: "blur(1px)",
              opacity: 0,
            }}
            animate={{
              backgroundColor: "#FFEDF1",
              filter: "blur(0px)",
              opacity: 1,
            }}
            exit={{
              backgroundColor: "#FE322A",
              filter: "blur(1px)",
              opacity: 0,
            }}
            className="px-3 py-3 rounded-full flex items-center gap-2 overflow-hidden"
            transition={{
              layout: { duration: 0.4, ease: [0.77, 0, 0.175, 1] },
              backgroundColor: { duration: 0.4, ease: "easeInOut" },
              filter: { duration: 0.2, ease: "easeInOut" },
              opacity: { duration: 0.2, ease: "easeIn" },
            }}
          >
            <motion.div
              initial={{ opacity: 0, scale: 0.5 }}
              animate={{ opacity: 1, scale: 1 }}
              exit={{ opacity: 0, scale: 0.5 }}
              transition={{ duration: 0.2, delay: 0.05 }}
              className="bg-[#FE322A] p-1.5 rounded-full flex items-center justify-center shrink-0"
            >
              <HugeiconsIcon icon={Undo03Icon} className="h-4 w-4 text-white" />
            </motion.div>

            <motion.span
              layoutId="buttonText"
              className="text-[#FE322A] font-medium flex"
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              transition={{ duration: 0.1 }}
            >
              {cancelText.split("").map((char, i) => (
                <motion.span
                  key={`cancel-${i}`}
                  initial={{ y: 20, opacity: 0, scale: 0.3 }}
                  animate={{ y: 0, opacity: 1, scale: 1 }}
                  exit={{ y: -20, opacity: 0, scale: 0.3 }}
                  transition={{
                    duration: 0.3,
                    delay: i * 0.006,
                    ease: [0.785, 0.135, 0.15, 0.86],
                  }}
                  style={{ display: "inline-block", whiteSpace: "pre" }}
                >
                  {char}
                </motion.span>
              ))}
            </motion.span>

            <motion.div
              className="bg-[#FE322A] text-white px-4 py-3 rounded-full text-sm font-semibold flex items-center justify-center relative overflow-hidden shrink-0 min-w-[32px]"
              initial={{ opacity: 0, scale: 0.5 }}
              animate={{ opacity: 1, scale: 1 }}
              exit={{ opacity: 0, scale: 0.5 }}
              transition={{ duration: 0.2, delay: 0.1 }}
            >
              <AnimatePresence mode="popLayout">
                <motion.span
                  key={count}
                  initial={{
                    opacity: 0,
                    y: 10,
                    scale: 0.8,
                  }}
                  animate={{ opacity: 1, y: 0, scale: 1 }}
                  exit={{
                    opacity: 0,
                    y: -10,
                    scale: 0.8,
                  }}
                  transition={{ duration: 0.2, ease: [0.33, 1, 0.68, 1] }}
                  className="absolute"
                >
                  {count}
                </motion.span>
              </AnimatePresence>
            </motion.div>
          </motion.button>
        )}
      </AnimatePresence>
    </div>
  );
};

export default DeleteButton;


demo.tsx
"use client";
import DeleteButton from "@/components/ui/delete-button";

export default function Demo() {
  return (
    <div className="flex items-center justify-center w-full min-h-screen bg-background p-8">
      <DeleteButton />
    </div>
  );
}

```

Install NPM dependencies:
```bash
motion, @hugeicons/react, @hugeicons/core-free-icons
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
