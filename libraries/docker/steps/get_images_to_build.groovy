package libraries.docker

/*
  returns an of the images that are built by this pipeline run.
  each image in the array is a hashmap with fields:
    registry: image registry
    repo: repo name
    tag: image tag
    context: directory context for docker build

  a docker build command would then be:
    docker build img.context -f pathDockerfile -t img.registry/img.repo:img.tag
*/
def call(){

    def (image_reg) = get_registry_info() // config.registry
    def path_prefix = config.repo_path_prefix ? config.repo_path_prefix + "/" : ""

    // String pathDockerfile = config.path_dockerfile ?: "**/Dockerfile"

    def build_strategies = [ "docker-compose", "modules", "multi", "dockerfile" ]
    if (config.build_strategy)
    if (!(config.build_strategy in build_strategies))
      error "build strategy: ${config.build_strategy} not one of ${build_strategies}"

    def images = []
    def listPath = []

    switch (config.build_strategy) {
      case "docker-compose":
        error "docker-compose build strategy not implemented yet"
        break
      case "modules":
        findFiles(glob: "**/*.Dockerfile").collect{ it.path.split("/").first() }.each{ service ->
          images.push([
            registry: image_reg,
            repo: "${path_prefix}${env.REPO_NAME}_${service}".toLowerCase(),
            tag: env.GIT_SHA,
            context: service
          ])
        }
        break
      case "multi":
        String pathDockerfile = config.path_dockerfile ?: "**/Dockerfile"
        findFiles(glob: pathDockerfile).collect{ it.path.split("/")[-1].split("[.]")[0]}.each { service ->
          // debug
          String service_name = (service == "Dockerfile") ? 'svc' : service

          images.push([
            registry: image_reg,
            repo: "${path_prefix}${env.REPO_NAME}_${service_name}".toLowerCase(),
            tag: "${env.BRANCH_NAME}-${env.GIT_SHA}",
            context: "." 
          ])
        }
        break
      case "dockerfile": //same as null/default case
      case null:
        images.push([
          registry: image_reg,
          repo: "${path_prefix}${env.REPO_NAME}".toLowerCase(),
          tag: env.GIT_SHA,
          context: "."
        ])
        break
    }

    return images
}
