export class ScriptedOperation {
  constructor(failuresBeforeSuccess) {
    this.failuresBeforeSuccess = failuresBeforeSuccess
    this.attempts = 0
  }

  call() {
    this.attempts++
    return this.attempts > this.failuresBeforeSuccess
  }
}

export function retry(maxAttempts, operation) {
  for (let attempt = 1; attempt <= maxAttempts; attempt++) {
    if (operation()) {
      return { ok: true, attempts: attempt }
    }
  }
  return { ok: false, attempts: maxAttempts }
}
