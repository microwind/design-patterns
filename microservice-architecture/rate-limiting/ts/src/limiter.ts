export class FixedWindowLimiter {
  private count = 0;

  constructor(private readonly limit: number) {}

  allow(): boolean {
    if (this.count >= this.limit) {
      return false;
    }
    this.count++;
    return true;
  }

  advanceWindow(): void {
    this.count = 0;
  }
}
