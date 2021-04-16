/* 
  author: namcxn
  date: Mar 02 00:00:00 +07 2021
 */

package libraries.utility

import org.jenkinsci.plugins.workflow.steps.FlowInterruptedException
import hudson.model.Result

void call(Map parameters, Closure body) {
  def label = "label-${UUID.randomUUID().toString()}"
  handleException {
        // githubAddTrigger()

        podTemplate(label) {
            // githubSetStatus(status: 'PENDING', description: 'Waiting for Kubernetes build pod to be available')
            node(POD_LABEL) {
               body()
            }
        }
    } { Exception exception ->
        throw exception
    } {
        discord()
    }
}
