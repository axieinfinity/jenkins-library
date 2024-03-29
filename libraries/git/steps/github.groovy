/* 
  author: namcxn
  date: Mar 02 00:00:00 +07 2021
 */

package libraries.git

// Import code required for GitHub functions
import org.kohsuke.github.GitHub

/*
    returns the name of the source branch in a Pull Request
    for example, in a MR from feature to development, the source branch
    would be "feature"
*/
void call() {
  this.get_source_branch()
}

def get_source_branch(){

  def cred_id = env.GIT_CREDENTIAL_ID

  withCredentials([usernamePassword(credentialsId: cred_id, passwordVariable: 'PAT', usernameVariable: 'USER')]) {
      return GitHub.connectUsingOAuth(PAT).
              getRepository("${env.ORG_NAME}/${env.REPO_NAME}")
              .getPullRequest(env.CHANGE_ID.toInteger())
              .getHead()
              .getRef()
  }
}
