void call(Closure body = null) {
    if (isPullRequestApproval()) {
        stage('Approve pull request') {
            boolean isMerged = false

            if (GIT_PREVIOUS_SUCCESSFUL_COMMIT == GIT_COMMIT) {
                println 'Prematurely notify GitHub that this build is successful, so its check can be shown as success in merge'
                githubNotify2()

                try {
                    println 'Squash and merge the pull request...'
                    githubRequest(
                        endpoint: "/repos/$org/$repo/pulls/$pr_id/merge",
                        method: 'PUT',
                        body: """{"commit_title":"$pr_title","sha":"$GIT_COMMIT","merge_method":"squash"}""",
                    )

                    isMerged = true
                } catch (exception) {
                    println hudson.Functions.printThrowable(exception)
                }
            }

            if (!isMerged) {
                String previousResult = currentBuild.previousBuild.result

                if (previousResult) {
                    println "Cannot merge the pull request, set build result to ${previousResult} based on the previous build..."
                    currentBuild.result = previousResult
                } else {
                    println "Cannot merge the pull request, set build result to UNSTABLE..."
                    currentBuild.result = 'UNSTABLE'
                }
            }
        }
    } else if (body) {
        body()
    }
}
