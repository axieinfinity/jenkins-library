package libraries.utility 

import org.jenkinsci.plugins.workflow.steps.FlowInterruptedException
import hudson.model.Result

void call(Closure body, Closure c4tch = null) {
  podTemplate(cloud: 'kubernetes', containers: [
    containerTemplate(name: 'docker-dind-gcloud', image: 'axieinfinity/axie-docker-dind-gcloud:latest', ttyEnabled: true, privileged: true, command: 'cat'),
    containerTemplate(name: 'helm-gcloud', image: 'axieinfinity/axie-helm3-gcloud:latest', ttyEnabled: true, privileged: true, command: 'cat'),
  ],
  volumes: [
    emptyDirVolume(mountPath: "/var/lib/docker", memory: false),
    hostPathVolume(hostPath: '/var/run/docker.sock', mountPath: '/var/run/docker.sock'),
    configMapVolume(mountPath: '/root/.gcloud/', configMapName: 'helm-secrets-confimap'),
  ]) {
    node(POD_LABEL) {
      try {
        body()
      } catch (exception) {
        if (exception instanceof FlowInterruptedException) {
            currentBuild.result = ((FlowInterruptedException) exception).result.toString()
        } else {
            currentBuild.result = Result.FAILURE.toString()
        }

        println "Got exception: ${hudson.Functions.printThrowable(exception)}"

        if (c4tch) {
            c4tch(exception)
        } else {
            throw exception
        }
      } finally {
        discord()
      }
    }
  }
}
