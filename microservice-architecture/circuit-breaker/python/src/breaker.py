class CircuitBreaker:
    def __init__(self, failure_threshold: int) -> None:
        self.failure_threshold = failure_threshold
        self.failures = 0
        self.state = "closed"

    def record_failure(self) -> None:
        if self.state == "closed":
            self.failures += 1
            if self.failures >= self.failure_threshold:
                self.state = "open"

    def probe(self, success: bool) -> None:
        if self.state != "open":
            return
        self.state = "half-open"
        if success:
            self.state = "closed"
            self.failures = 0
        else:
            self.state = "open"
