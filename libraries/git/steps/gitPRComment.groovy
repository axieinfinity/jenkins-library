package libraries.git

import hudson.model.*

// Always run at the end of pipeline
// https://boozallen.github.io/sdp-docs/jte/1.7.1/library-development/lifecycle_hooks.html
// @CleanUp	
void call(){

//node{
    // Check if this is pull request
    if (env.CHANGE_TARGET){
        def repository_url = scm.userRemoteConfigs[0].url
        def repository_name = repository_url.replace("https://github.com/","").replace(".git","")
        def ghprbPullId = env.CHANGE_ID
        def text_pr = "Jenkins job ${JOB_NAME} from [build ${BUILD_NUMBER}](${BUILD_URL}) status ```${currentBuild.currentResult}``` ."
    
        println "Repo name: ${repository_name} "
        println "Github pull request ID : ${ghprbPullId} "

        withCredentials([string(credentialsId: 'github-token', variable: 'GITHUB_TOKEN')]) {
            sh "curl -i -u huy-axie:${GITHUB_TOKEN} -X POST -d '{\"body\": \"${text_pr}\"}' \"https://api.github.com/repos/${repository_name}/issues/${ghprbPullId}/comments\""
        }
    } else {
        println "Not a Pull request job, skipping..."
        return
    }
//}
}