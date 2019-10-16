import hudson.model.Result
import org.jenkinsci.plugins.workflow.steps.FlowInterruptedException

void call(Closure body, Closure c4tch = null, Closure f1nally = null) {
    try {
        body()
    } catch (exception) {
        if (exception instanceof FlowInterruptedException) {
            currentBuild.result = ((FlowInterruptedException) exception).result.toString()
        } else {
            currentBuild.result = Result.FAILURE.toString()
        }

        println "Got exception: ${hudson.Functions.printThrowable(exception)}"

        if (c4tch) {
            c4tch(exception)
        } else {
            throw exception
        }
    } finally {
        if (f1nally) {
            f1nally()
        }
    }
}
