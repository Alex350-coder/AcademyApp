import { Navbar } from '@/landing/components/Navbar';
import { HeroSection } from '@/landing/sections/HeroSection';
import { BenefitsSection } from '@/landing/sections/BenefitsSection';
import { FeaturesSection } from '@/landing/sections/FeaturesSection';
import { DashboardPreviewSection } from '@/landing/sections/DashboardPreviewSection';
import { TestimonialsSection } from '@/landing/sections/TestimonialsSection';
import { FAQSection } from '@/landing/sections/FAQSection';
import { FinalCTASection } from '@/landing/sections/FinalCTASection';
import { Footer } from '@/landing/components/Footer';
import { AuroraBackground } from '@/components/ui/aurora-background';

export default function LandingPage() {
  return (
    <div className="relative min-h-screen bg-background/90 text-text">
      <AuroraBackground />
      <Navbar />
      <HeroSection />
      <BenefitsSection />
      <FeaturesSection />
      <DashboardPreviewSection />
      <TestimonialsSection />
      <FAQSection />
      <FinalCTASection />
      <Footer />
    </div>
  );
}
