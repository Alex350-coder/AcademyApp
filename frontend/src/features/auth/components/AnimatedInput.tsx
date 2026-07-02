import { forwardRef, useState, type InputHTMLAttributes } from 'react';
import { motion, useMotionTemplate, useMotionValue } from 'framer-motion';
import { Eye, EyeOff } from 'lucide-react';
import { cn } from '@/lib/utils';

interface AnimatedInputProps extends InputHTMLAttributes<HTMLInputElement> {
  label: string;
  error?: string;
}

export const AnimatedInput = forwardRef<HTMLInputElement, AnimatedInputProps>(function AnimatedInput(
  { label, error, className, type, id, ...props },
  ref,
) {
  const [visible, setVisible] = useState(false);
  const [focused, setFocused] = useState(false);
  const radius = 100;
  const mouseX = useMotionValue(0);
  const mouseY = useMotionValue(0);

  function handleMouseMove(e: React.MouseEvent<HTMLDivElement>) {
    const { left, top } = e.currentTarget.getBoundingClientRect();
    mouseX.set(e.clientX - left);
    mouseY.set(e.clientY - top);
  }

  const isPassword = type === 'password';

  return (
    <div className="flex flex-col gap-1.5">
      <label htmlFor={id} className="text-sm font-medium text-text">
        {label}
      </label>
      <motion.div
        style={{
          background: useMotionTemplate`
            radial-gradient(
              ${focused ? radius + 'px' : '0px'} circle at ${mouseX}px ${mouseY}px,
              var(--color-primary),
              transparent 80%
            )
          `,
        }}
        onMouseMove={handleMouseMove}
        onMouseEnter={() => setVisible(true)}
        onMouseLeave={() => setVisible(false)}
        className="group/input rounded-lg p-[2px] transition duration-300"
      >
        <div className="relative">
          <input
            ref={ref}
            id={id}
            type={isPassword ? (visible ? 'text' : 'password') : type}
            className={cn(
              'shadow-input flex h-10 w-full rounded-md border bg-background px-3 py-2 text-sm text-text transition-shadow duration-400 group-hover/input:shadow-none',
              'file:border-0 file:bg-transparent file:text-sm file:font-medium',
              'placeholder:text-muted',
              'focus-visible:ring-[2px] focus-visible:ring-primary focus-visible:outline-none',
              'disabled:cursor-not-allowed disabled:opacity-50',
              isPassword && 'pr-10',
              error && 'ring-2 ring-danger',
              className,
            )}
            onFocus={() => setFocused(true)}
            onBlur={() => setFocused(false)}
            {...props}
          />
          {isPassword && (
            <button
              type="button"
              onClick={() => setVisible(!visible)}
              onMouseDown={(e) => e.preventDefault()}
              className="absolute inset-y-0 right-0 pr-3 flex items-center text-muted hover:text-text transition-colors"
              tabIndex={-1}
            >
              {visible ? <EyeOff size={18} /> : <Eye size={18} />}
            </button>
          )}
        </div>
      </motion.div>
      {error && (
        <p className="text-xs text-danger mt-0.5">{error}</p>
      )}
    </div>
  );
});
