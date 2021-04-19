/* 
  author: namcxn
  date: Mar 02 00:00:00 +07 2021
 */

package libraries.utility

import org.jenkinsci.plugins.workflow.steps.FlowInterruptedException
import hudson.model.Result

void call(Map parameters, Closure body, Closure c4tch = null) {
    def label = "${JOB_NAME}-${UUID.randomUUID().toString()}"
    podTemplate(parameters) {
        node(POD_LABEL) {
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
                discord()
            }
        }
    }
}
