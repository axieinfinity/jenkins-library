import java.util.regex.Matcher
import jenkins.plugins.http_request.ResponseContentSupplier
import net.sf.json.JSONArray

void call(Closure body) {
    if (isPullRequestApproval()) {
        stage('Approve pull request') {
            boolean isMerged = false

            if (GIT_PREVIOUS_SUCCESSFUL_COMMIT == GIT_COMMIT) {
                try {
                    println 'The current commit has passed checks previously, now merge the pull request'
                    squashAndMerge(org, repo, pr_id, pr_title)
                    isMerged = true
                } catch (exception) {
                    println "Got exception while trying to merge: ${hudson.Functions.printThrowable(exception)}"
                }
            } else {
                println 'The current commit has not passed checks previously, so ignore'
            }

            if (!isMerged) {
                String previousResult = currentBuild.previousBuild.result

                if (previousResult) {
                    println "The pull request was not merged, set build result to $previousResult based on the previous build"
                    currentBuild.result = previousResult
                } else {
                    println 'The pull request was not merged, set build result to UNSTABLE'
                    currentBuild.result = 'UNSTABLE'
                }
            }
        }
    } else {
        body()

        if (env.CHANGE_ID) {
            stage('Check pull request approvals') {
                List orgAndRepo = getOrgAndRepo(CHANGE_URL)

                if (orgAndRepo) {
                    String org = orgAndRepo[0]
                    String repo = orgAndRepo[1]

                    try {
                        // Should handle pull request reviews passed the first page when this scales
                        ResponseContentSupplier response = githubRequest(
                            endpoint: "/repos/$org/$repo/pulls/$CHANGE_ID/reviews",
                        )

                        JSONArray reviews = readJSON(text: response.content)

                        for (review in reviews) {
                            if (review.state == 'APPROVED') {
                                println 'We have at least one pull request approval, now merge the pull request'
                                squashAndMerge(org, repo, CHANGE_ID, CHANGE_TITLE)
                                break;
                            }
                        }
                    } catch (exception) {
                        println "Got exception while trying to merge: ${hudson.Functions.printThrowable(exception)}"
                    }
                } else {
                    println "Cannot extract organization and repository from CHANGE_URL: $CHANGE_URL"
                }
            }
        }
    }
}

@NonCPS
List getOrgAndRepo(String prUrl) {
    Matcher matcher = prUrl =~ /\/([^\/]+)\/([^\/]+)\/pull\/\d+$/
    matcher.find() ? [matcher[0][1], matcher[0][2]] : null
}

void squashAndMerge(String org, String repo, String prId, String prTitle) {
    println 'Prematurely notify GitHub that this build is successful, so its check can be shown as a success in the upcoming merge'
    githubSetStatus()

    println 'Squash and merge the pull request'
    githubRequest(
        endpoint: "/repos/$org/$repo/pulls/$prId/merge",
        method: 'PUT',
        body: """{"commit_title":"$prTitle","sha":"$GIT_COMMIT","merge_method":"squash"}""",
    )
}
