/* 
  author: namcxn
  date: Mar 02 00:00:00 +07 2021
 */

package libraries.git

void call(Map args = [:], body) {

  // Pre-check pull request to make sure hasChangesIn() working correctly.
  if((env.CHANGE_TARGET) && !env.GIT_PR_VALID.toBoolean() ){
      println "WARNING: This PR is not update with master, please rebase."
      return
  }
  def dir_target = hasChangesIn(args.in)

  println "Running because get dir target is ${dir_target}"
  
  // do nothing if dir !true
  if (args.in)
  if( dir_target == false )
    return

  body()
}

def hasChangesIn(String module) {

    def target_branch_name = env.BRANCH_NAME

    def target_branch = sh(
        returnStdout: true,
        script: "git rev-parse remotes/origin/${target_branch_name}"
    ).trim()

    // Gets commit hash of HEAD commit. Jenkins will try to merge master into
    // HEAD before running checks. If this is a fast-forward merge, HEAD does
    // not change. If it is not a fast-forward merge, a new commit becomes HEAD
    // so we check for the non-master parent commit hash to get the original
    // HEAD. Jenkins does not save this hash in an environment variable.

    // If this is a pull request
    if(env.CHANGE_TARGET){

      // branch pull request want to merge.
      def dest_branch = env.CHANGE_TARGET

      def HEAD = sh(
        returnStdout: true,
        script: "git rev-parse origin/${dest_branch}"
      ).trim()

      return sh (
        returnStatus: true,
        script: "git diff --name-only ${target_branch} ${HEAD} | grep -i '${module}'"
      ) == 0
    
    // If this is not a pull request
    }else{
      return sh (
        returnStatus: true,
        script: "git diff --name-only ${target_branch} ${target_branch} | grep -i '${module}'"
      ) == 0
    }
}
