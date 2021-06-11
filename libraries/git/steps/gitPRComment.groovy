package libraries.git

import hudson.model.*

// Always run at the end of pipeline
// https://boozallen.github.io/sdp-docs/jte/1.7.1/library-development/lifecycle_hooks.html
// @CleanUp	
void call(){
    // Check if this is pull request
    if (env.CHANGE_TARGET){

        def repository_url = scm.userRemoteConfigs[0].url
        def repository_name = repository_url.replace("https://github.com/","").replace(".git","")
        def ghprbPullId = env.CHANGE_ID
        def text_pr = "Jenkins job ${JOB_NAME} from [build ${BUILD_NUMBER}](${BUILD_URL}) status ```${currentBuild.currentResult}``` ."

        def commentID

        println "Repo name: ${repository_name} "
        println "Github pull request ID : ${ghprbPullId} "
        
        // get app repo
        def (repo_owner, repo_name) = repository_name.tokenize('/')

        // Find status comment
        withCredentials([string(credentialsId: 'github-token', variable: 'GITHUB_TOKEN')]) {
            commentID = sh (
                script: """
                        curl -s -H \"Authorization: Token ${GITHUB_TOKEN}\" \
                        -d '{ "query": "query { repository(owner: \\"${repo_owner}\\", name: \\"${repo_name}\\") { pullRequest(number: '${ghprbPullId}') { comments(first: 10) { edges { node { body id } } } } } }" }' https://api.github.com/graphql | \
                        jq -r '.data.repository.pullRequest.comments.edges[]?|select(.node.body | contains(\"${JOB_NAME}\")) | .node.id '
                        """,
                returnStdout: true
                ).trim()
        }

        println "${commentID}"

        // If commentID found
        if (commentID) {

            withCredentials([string(credentialsId: 'github-token', variable: 'GITHUB_TOKEN')]) {
                sh """
                curl -s -H \"Authorization: Token ${GITHUB_TOKEN}\" \
                -d '{ "query": "mutation { updateIssueComment(input: { id: \"${commentID}\" , body: \"${text_pr}\"}) { issueComment { updatedAt } } }" }' \
                https://api.github.com/graphql
                """
            }  
           
        } else {
            // If no, create the new one
            withCredentials([string(credentialsId: 'github-token', variable: 'GITHUB_TOKEN')]) {
                sh "curl -i -u huy-axie:${GITHUB_TOKEN} -X POST -d '{\"body\": \"${text_pr}\"}' \"https://api.github.com/repos/${repository_name}/issues/${ghprbPullId}/comments\""
            }
           
        }

    } else {
        println "Not a Pull request job, skipping..."
        return
    }
}
