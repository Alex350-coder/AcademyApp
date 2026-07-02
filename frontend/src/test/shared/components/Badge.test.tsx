import { render, screen } from '@testing-library/react';
import { Badge } from '@/shared/components/Badge';

describe('Badge', () => {
  it('renders text', () => {
    render(<Badge>Active</Badge>);
    expect(screen.getByText('Active')).toBeInTheDocument();
  });

  it('applies correct variant styles', () => {
    const { rerender } = render(<Badge variant="success">Success</Badge>);
    expect(screen.getByText('Success').className).toContain('bg-success-bg');

    rerender(<Badge variant="warning">Warning</Badge>);
    expect(screen.getByText('Warning').className).toContain('bg-warning-bg');

    rerender(<Badge variant="danger">Danger</Badge>);
    expect(screen.getByText('Danger').className).toContain('bg-danger-bg');

    rerender(<Badge variant="info">Info</Badge>);
    expect(screen.getByText('Info').className).toContain('bg-primary/10');

    rerender(<Badge>Default</Badge>);
    expect(screen.getByText('Default').className).toContain('bg-surface-hover');
  });

  it('applies additional className', () => {
    render(<Badge className="extra-class">Styled</Badge>);
    expect(screen.getByText('Styled').className).toContain('extra-class');
  });
});
