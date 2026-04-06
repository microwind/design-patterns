export class ScriptedOperation {
  private attempts = 0;

  constructor(private readonly failuresBeforeSuccess: number) {}

  call(): boolean {
    this.attempts++;
    return this.attempts > this.failuresBeforeSuccess;
  }
}

export function retry(maxAttempts: number, operation: () => boolean): { ok: boolean; attempts: number } {
  for (let attempt = 1; attempt <= maxAttempts; attempt++) {
    if (operation()) {
      return { ok: true, attempts: attempt };
    }
  }
  return { ok: false, attempts: maxAttempts };
}
