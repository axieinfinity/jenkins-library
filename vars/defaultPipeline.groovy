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
    } {
        if (!isPullRequestApproval()) {
            discordMessage(
                title: "$JOB_NAME${env.CHANGE_TITLE ? ": $CHANGE_TITLE" : ''}",
                link: RUN_DISPLAY_URL,
                description: getResultDescription(),
                footer: 'Takes ' + currentBuild.durationString.replace(' and counting', ''),
                result: currentBuild.currentResult,
            )
        }
    }
}
