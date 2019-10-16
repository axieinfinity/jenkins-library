void call(Closure body, Closure c4tch = null, Closure f1nally = null) {
    try {
        body()
    } catch (org.jenkinsci.plugins.workflow.steps.FlowInterruptedException exception) {
        currentBuild.result = exception.result.toString()

        if (c4tch) {
            c4tch(exception)
        } else {
            throw exception
        }
    } catch (exception) {
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
