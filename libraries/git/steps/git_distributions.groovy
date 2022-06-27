package libraries.git

import hudson.AbortException

/*
  Validate library configuration
*/
// @init
void call(){

    /*
      define a map of distributions and a closure for their own validations
    */
    def options = [
        "github": { c -> println "github config is ${c}"},
    ]

    def submap = config.subMap(options.keySet())
    if(submap.size() != 1){
        error "you must configure one distribution option, currently: ${submap.keySet()}"
    }

    // get the distribution
    String dist = submap.keySet().first()
    // invoke the distribution closure
    options[dist](config[dist])

    env.GIT_LIBRARY_DISTRUBITION = dist
    this.init_env()
}

// Initialize Git configuration of env vars
void init_env(){
    stage "Checkout source code", {
        cleanWs()
        try{
            checkout scm
        }catch(AbortException ex) {
            println "scm var not present, skipping source code checkout"
        }catch(err){
          println "exception ${err}"
        }

        env.GIT_URL = scm.getUserRemoteConfigs()[0].getUrl()
        env.GIT_CREDENTIAL_ID = scm.getUserRemoteConfigs()[0].credentialsId.toString()
        def parts = env.GIT_URL.split("/")
        for (part in parts){
            parts = parts.drop(1)
            if (part.contains(".")) break
        }

        env.ORG_NAME = parts.getAt(0)
        env.REPO_NAME = parts[1..-1].join("/") - ".git"

        def target_branch_name = env.BRANCH_NAME

       // Check if checking out a tags or a branch
        if (isTags()){
            env.GIT_SHA = sh(script: "git rev-parse --short refs/tags/${target_branch_name}", returnStdout: true).trim()
        }else{
            env.GIT_SHA = sh(script: "git rev-parse --short remotes/origin/${target_branch_name}", returnStdout: true).trim()
        }

        if (env.CHANGE_TARGET){
            env.GIT_BUILD_CAUSE = "pr"
            if (isUpdatedWithTarget()){
                env.GIT_PR_VALID = true
            }else{
                env.GIT_PR_VALID = false
            }

        } else {
            env.GIT_BUILD_CAUSE = sh (
              script: 'git rev-list HEAD --parents -1 | wc -w', // will have 2 shas if commit, 3 or more if merge
              returnStdout: true
            ).trim().toInteger() > 2 ? "merge" : "commit"
        }

        println "Found Git Build Cause: ${env.GIT_BUILD_CAUSE}"
    }
    return
}

def fetch(){
    return getBinding().getStep(env.GIT_LIBRARY_DISTRUBITION)
}


// Check if Pull request update with target branch
boolean isUpdatedWithTarget() {

    String gitMergeBaseCommit = sh(script: "git merge-base remotes/origin/${env.CHANGE_TARGET} remotes/origin/${BRANCH_NAME}", returnStdout: true).trim()

    String headOrigin = sh(script: "git rev-parse remotes/origin/${env.CHANGE_TARGET}", returnStdout: true).trim()

    if (headOrigin.equals(gitMergeBaseCommit)){
        return true
    }
    return false
}

// Check if this is a tag
boolean isTags() {
    // Always return true
    String currentTag = sh(script:"git describe --tags || true", returnStdout: true).trim()

    String currtentBranch = env.BRANCH_NAME

    if (currentTag.equals(currtentBranch)){
        return true
    }
    return false
}
