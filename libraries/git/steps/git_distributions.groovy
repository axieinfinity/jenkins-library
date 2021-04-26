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
    //node{
        // try { unstash "workspace" }
        // catch(ignored) { 
        //   println "'workspace' stash not present. Skipping git library environment variable initialization. To change this behavior, ensure the 'sdp' library is loaded"
        //   return
        // }
    stage "Checkout source code", {
        cleanWs()
        try{
            checkout scm
        }catch(AbortException ex) {
            println "scm var not present, skipping source code checkout" 
        }catch(err){
          println "exception ${err}" 
        }
        
        // stash name: 'workspace', allowEmpty: true, useDefaultExcludes: false

        env.GIT_URL = scm.getUserRemoteConfigs()[0].getUrl()
        env.GIT_CREDENTIAL_ID = scm.getUserRemoteConfigs()[0].credentialsId.toString()
        def parts = env.GIT_URL.split("/")
        for (part in parts){
            parts = parts.drop(1)
            if (part.contains(".")) break
        }
        env.ORG_NAME = parts.getAt(0)
        env.REPO_NAME = parts[1..-1].join("/") - ".git"
        env.GIT_SHA = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()

        if (env.CHANGE_TARGET){
            env.GIT_BUILD_CAUSE = "pr"
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
    return getStep(env.GIT_LIBRARY_DISTRUBITION)
}
