export class CircuitBreaker {
  private failures = 0;
  state = "closed";

  constructor(private readonly failureThreshold: number) {}

  recordFailure(): void {
    if (this.state === "closed") {
      this.failures++;
      if (this.failures >= this.failureThreshold) {
        this.state = "open";
      }
    }
  }

  probe(success: boolean): void {
    if (this.state !== "open") return;
    this.state = "half-open";
    if (success) {
      this.state = "closed";
      this.failures = 0;
    } else {
      this.state = "open";
    }
  }
}
