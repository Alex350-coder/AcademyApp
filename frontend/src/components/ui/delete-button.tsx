"use client";

import { cn } from "@/lib/utils";
import { Undo03Icon } from "@hugeicons/core-free-icons";
import { HugeiconsIcon } from "@hugeicons/react";
import { AnimatePresence, motion } from "motion/react";
import { useEffect, useState } from "react";

interface DeleteButtonProps {
  onDelete?: () => void;
  deleteText?: string;
  cancelText?: string;
  countdownSeconds?: number;
  className?: string;
}

export function DeleteButton({
  onDelete,
  deleteText = "Delete Account",
  cancelText = "Cancel Deletion",
  countdownSeconds = 10,
  className,
}: DeleteButtonProps) {
  const [isDeleting, setIsDeleting] = useState(false);
  const [count, setCount] = useState(countdownSeconds);
  const [isAnimating, setIsAnimating] = useState(false);

  useEffect(() => {
    if (!isDeleting) return;
    if (count === 0) {
      onDelete?.();
      return;
    }
    const timer = setTimeout(() => setCount((c) => c - 1), 1000);
    return () => clearTimeout(timer);
  }, [isDeleting, count, onDelete]);

  const handleClick = (newState: boolean) => {
    if (isAnimating) return;
    setIsAnimating(true);
    setIsDeleting(newState);
    if (newState) setCount(countdownSeconds);
    setTimeout(() => setIsAnimating(false), 400);
  };

  return (
    <div className={cn("flex items-center justify-center", className)}>
      <AnimatePresence mode="popLayout" initial={false}>
        {!isDeleting ? (
          <motion.button
            key="delete"
            layoutId="deleteButton"
            onClick={() => handleClick(true)}
            whileTap={{ scale: 0.95 }}
            style={{ pointerEvents: isAnimating ? "none" : "auto" }}
            initial={{ backgroundColor: "#FEF2F2", filter: "blur(1px)", opacity: 1 }}
            animate={{ backgroundColor: "#EF4444", filter: "blur(0px)", opacity: 1 }}
            exit={{ backgroundColor: "#FEF2F2", filter: "blur(1px)", opacity: 0 }}
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
          <motion.button
            key="cancel"
            layoutId="deleteButton"
            onClick={() => handleClick(false)}
            whileTap={{ scale: 0.95 }}
            style={{ pointerEvents: isAnimating ? "none" : "auto" }}
            initial={{ backgroundColor: "#EF4444", filter: "blur(1px)", opacity: 0 }}
            animate={{ backgroundColor: "#FEF2F2", filter: "blur(0px)", opacity: 1 }}
            exit={{ backgroundColor: "#EF4444", filter: "blur(1px)", opacity: 0 }}
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
              className="bg-danger p-1.5 rounded-full flex items-center justify-center shrink-0"
            >
              <HugeiconsIcon icon={Undo03Icon} className="h-4 w-4 text-white" />
            </motion.div>

            <motion.span
              layoutId="buttonText"
              className="text-danger font-medium flex"
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
              className="bg-danger text-white px-4 py-3 rounded-full text-sm font-semibold flex items-center justify-center relative overflow-hidden shrink-0 min-w-[32px]"
              initial={{ opacity: 0, scale: 0.5 }}
              animate={{ opacity: 1, scale: 1 }}
              exit={{ opacity: 0, scale: 0.5 }}
              transition={{ duration: 0.2, delay: 0.1 }}
            >
              <AnimatePresence mode="popLayout">
                <motion.span
                  key={count}
                  initial={{ opacity: 0, y: 10, scale: 0.8 }}
                  animate={{ opacity: 1, y: 0, scale: 1 }}
                  exit={{ opacity: 0, y: -10, scale: 0.8 }}
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
}

export default DeleteButton;
