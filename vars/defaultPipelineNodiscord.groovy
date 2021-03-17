void call(Map parameters, Closure body) {
    handleException {
        githubAddTrigger()

        podTemplate(parameters) {
            githubSetStatus(status: 'PENDING', description: 'Waiting for Kubernetes build pod to be available')

            node(POD_LABEL) {
                stage('Checkout code') {
                    githubSetStatus(status: 'PENDING', description: 'Checking out code')
                    readTrusted 'Jenkinsfile'
                    checkoutSetEnv()
                }

                handlePullRequestApprovalFor {
                    body()
                }
            }
        }
    } { Exception exception ->
        throw exception
    }
}
