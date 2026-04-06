export class FixedWindowLimiter {
  constructor(limit) {
    this.limit = limit
    this.count = 0
  }

  allow() {
    if (this.count >= this.limit) {
      return false
    }
    this.count++
    return true
  }

  advanceWindow() {
    this.count = 0
  }
}
