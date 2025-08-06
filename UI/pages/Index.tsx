import { SolarCalculator } from '@/components/SolarCalculator';
import heroImage from '@/assets/solar-hero.jpg';
import solarIcon from '@/assets/solar-icon.png';

const Index = () => {
  return (
    <div className="min-h-screen bg-gradient-sky">
      {/* Hero Section */}
      <div className="relative overflow-hidden">
        <div 
          className="absolute inset-0 z-0"
          style={{
            backgroundImage: `url(${heroImage})`,
            backgroundSize: 'cover',
            backgroundPosition: 'center',
            filter: 'brightness(0.3)'
          }}
        />
        <div className="relative z-10 container mx-auto px-4 py-20 text-center animate-fade-in">
          <div className="flex items-center justify-center gap-4 mb-6 animate-scale-in">
            <img src={solarIcon} alt="Solar Icon" className="w-16 h-16 animate-solar-glow" />
            <h1 className="text-5xl md:text-7xl font-bold text-white animate-slide-up">
              Solar<span className="text-primary">IQ</span>
            </h1>
          </div>
          <p className="text-xl md:text-2xl text-white/90 mb-8 max-w-3xl mx-auto animate-slide-up [animation-delay:200ms]">
            Harness the power of AI to calculate your solar potential, savings, and environmental impact with precision
          </p>
          <div className="bg-white/10 backdrop-blur-sm rounded-lg p-6 max-w-2xl mx-auto border border-white/20 animate-scale-in [animation-delay:400ms] hover:bg-white/15 transition-all duration-300">
            <p className="text-white/80 mb-4 animate-fade-in [animation-delay:600ms]">
              Our advanced AI calculator uses the same powerful algorithms as professional solar installations
            </p>
            <div className="flex flex-wrap justify-center gap-4 text-sm text-white/70 animate-fade-in [animation-delay:800ms]">
              <span className="hover:text-white transition-colors cursor-default">✨ Real-time calculations</span>
              <span className="hover:text-white transition-colors cursor-default">🌱 Environmental impact</span>
              <span className="hover:text-white transition-colors cursor-default">💸 ₹ Cost savings analysis</span>
              <span className="hover:text-white transition-colors cursor-default">📊 Detailed reports</span>
            </div>
          </div>
        </div>
      </div>

      {/* Calculator Section */}
      <div className="container mx-auto px-4 py-12">
        <SolarCalculator />
      </div>

      {/* Footer */}
      <footer className="bg-foreground/5 py-8 text-center text-muted-foreground">
        <p>© 2024 SolarIQ AI. Empowering sustainable energy decisions through artificial intelligence.</p>
      </footer>
    </div>
  );
};

export default Index;
