package libraries.ssh 

void call(app_env) {
  stage("Deploy to ${app_env.long_name}") {
    def remote = [:]
    remote.name = app_env.long_name
    remote.host = app_env.ip
    remote.allowAnyHosts = true
    /*
       ssh credential 
    */
    String ssh_credential = app_env.ssh_credential ?:
                         config.ssh_credential ?:
                         {error "SSH credentail not found"}()
    String user_name = app_env.ssh_user ?:
                       config.ssh_user ?: "deploy"
    // JSch does not support OpenSSH key format.
    // just the following command: ssh-keygen -p -m pem -f id_rsa
    withCredentials([sshUserPrivateKey(credentialsId: ssh_credential,
            keyFileVariable: 'IDENTIY_FILE',
            passphraseVariable: '',
            usernameVariable: 'USERNAME')]){
      
      remote.user = user_name
      remote.identityFile = "${IDENTIY_FILE}"

      String release = config.method_release ?: "command"
      if ( release == "command") {
        echo "Release to ${app_env}"
        sshCommand remote: remote, command: 'uname -r'
        //command_release()
      }
    }
  }
}

void command_release() {
  def images = get_images_to_build()

  images.each{ img ->
    
  }
  String docker_command = 

  sshCommand remote: remote, command: "${docker_command}"
}

void execute_release() {

}
