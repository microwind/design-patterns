package src

type ScriptedOperation struct {
	failuresBeforeSuccess int
	attempts             int
}

func NewScriptedOperation(failuresBeforeSuccess int) *ScriptedOperation {
	return &ScriptedOperation{failuresBeforeSuccess: failuresBeforeSuccess}
}

func (o *ScriptedOperation) Call() bool {
	o.attempts++
	return o.attempts > o.failuresBeforeSuccess
}

func Retry(maxAttempts int, operation func() bool) (bool, int) {
	for attempt := 1; attempt <= maxAttempts; attempt++ {
		if operation() {
			return true, attempt
		}
	}
	return false, maxAttempts
}
