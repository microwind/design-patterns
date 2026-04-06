class ScriptedOperation:
    def __init__(self, failures_before_success: int) -> None:
        self.failures_before_success = failures_before_success
        self.attempts = 0

    def call(self) -> bool:
        self.attempts += 1
        return self.attempts > self.failures_before_success


def retry(max_attempts: int, operation) -> tuple[bool, int]:
    for attempt in range(1, max_attempts + 1):
        if operation():
            return True, attempt
    return False, max_attempts
