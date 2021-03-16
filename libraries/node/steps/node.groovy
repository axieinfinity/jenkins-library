/* 
  author: namcxn
  date: Mar 02 00:00:00 +07 2021
 */

void call(Closure body){
    podTemplate(yaml: """
spec:
  containers:
  - name: docker-dind-gcloud
    image: axieinfinity/axie-docker-dind-gcloud:latest
    securityContext:
      runAsUser: 0
    command:
    - cat
    tty: true
    volumeMounts:
    - name: ssh-deploy
      readOnly: true
      mountPath: "/root/.ssh/"
  volumes:
  - name: ssh-deploy
    secret:
      secretName: ssh-keys-deploys
      defaultMode: 0600
      items:
      - key: config
        path: config
"""
    ){
        steps.node(POD_LABEL){
            body()
        }
    }
}
