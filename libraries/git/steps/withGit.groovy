/*
  Copyright © 2018 Skymavis All Rights Reserved.
*/
package libraries.git

void call(Map args, Closure body){

  // check required parameters
  if (!args.url || !args.cred)
    error """
    withGit syntax error.
    Input Parameters:
      url: https git url to repository (required)
      cred: jenkins credential ID for github. (required)
      branch: branch in the repository to checkout. defaults to master. (optional)
    """

  withCredentials([usernamePassword(credentialsId: args.cred, passwordVariable: 'PASS', usernameVariable: 'USER')]) {
    repo = args.url.split("/").last() - ".git"
    withEnv(["git_url_with_creds=${args.url.replaceFirst("://","://${USER}:${PASS}@")}"]) {
        sh "rm -rf ${repo}"
        sh "set +x && git clone ${env.git_url_with_creds}"
        dir(repo){
          sh "git checkout ${args.branch ?: "master"}"
          push = "push"
          body.resolveStrategy = Closure.DELEGATE_FIRST
          body.delegate = this
          body.run()
        }
    }
  }
}
