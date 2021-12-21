/* 
  author: namcxn
  date: Mar 02 00:00:00 +07 2021
 */

package libraries.kubernetes

void call(app_env) {
  stage "Deploy to ${app_env.cluster_name}", {

    def env  = app_env.environment ?:
              {error "Application Environment"}()

    def cluster_name = app_env.cluster_name ?:
                      config.cluster_name ?:
                      {error "k8s cluster not defined in library config or application environment config"}()

    def cluster_zone = app_env.cluster_zone ?:
                      config.cluster_zone ?:
                      {error "k8s cluster not defined in library config or application environment config"}()

    def project_id =  app_env.project_id ?:
                      config.project_id ?:
                      {error "k8s cluster not defined in library config or application environment config"}()

    def app_cred = app_env.app_cred ?:
                  config.app_cred ?:
                  {error "k8s cluster not defined in library config or application environment config"}()

    def app_cred_decrypt = app_env.app_cred_decrypt ?:
                  config.app_cred_decrypt ?:
                  {error "k8s cluster not defined in library config or application environment config"}()   

    def valuesPath = app_env.value_path ?:
                    {error "k8s cluster not defined in library config or application environment config"}()
    /*
        helm release name.
        will use "release_name" if present on app env object
        or will use "short_name" if present on app_env object.
        will fail otherwise.
      */
    def release = app_env.release_name ?:
                  "${JOB_NAME}" ?:
                  {error "App Env Must Specify release_name"}()

    /*
      helm chart version
    */
    def chart_ver = app_env.release_ver ?:
                    "0.0.1" ?:
                    {error "App Env Must Specify release_ver"}()
    /*
      Pre yaml apply
    */
    def additionalYaml = app_env.add_yaml ?:
                    " " ?:
                    {error "App Env Must Specify add_yaml"}()
    /*
        // k8s context
    def k8s_context = app_env.k8s_context ?:
                      config.k8s_context            ?:
                      {error "Kubernetes Context Not Defined"}()
      */

    def images = get_images_to_build()
    images.each { img ->
      sh "gcloud auth activate-service-account --key-file=${app_cred}"
      sh "gcloud container clusters get-credentials ${cluster_name} --zone ${cluster_zone} --project ${project_id}"

      // Check if we need to add addtitional yaml
      if (isYamlExists("${additionalYaml}")){
        def listYaml = additionalYaml.tokenize( ',' )
        
        listYaml.each { yaml ->
          sh "kubectl apply -f ${yaml}"
        }
      }

      // Deploy application
      sh label: 'Deploy to development', script: '''
                                  kubectl cluster-info
                                  helm repo add skymavis https://charts.skymavis.one
                                  helm repo update
                                  '''
      sh "export GOOGLE_APPLICATION_CREDENTIALS=${app_cred_decrypt} && helm secrets upgrade --atomic --install --debug ${release} skymavis/k8s-service --version ${chart_ver} --namespace ${env} --set containerImage.repository=${img.registry}/${img.repo} --set containerImage.tag=${img.tag} -f ${valuesPath}/values.yaml -f ${valuesPath}/secrets.yaml"
      }
  }

}

// Pre check yaml
boolean isYamlExists(filePath) {
  // not null
  if (filePath?.trim()) {
    return true
  }
  return false
}