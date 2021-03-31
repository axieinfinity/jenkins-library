/* 
  author: namcxn
  date: Mar 02 00:00:00 +07 2021
 */

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

    String cmd_release = app_env.cmd_release ?:
                        {error "Not found command relaease"}

    String cmd_purge = app_env.cmd_purge ?:
                       {error "Not found command purge container"}
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
        /*
          for test/debug
          sshCommand remote: remote, command: 'uname -r'
        */
        this.purge_container remote, cmd_purge
        this.command_release remote, cmd_release
      }
    }
  }
}

void purge_container(remote, cmd_purge){
  sshCommand remote: remote, command: cmd_purge
}

void command_release(remote, cmd_release) {
  def images = get_images_to_build()

  images.each{ img ->
    
    String docker_command = "${cmd_release} ${img.registry}/${img.repo}:${img.tag}"
    sshCommand remote: remote, command: "${docker_command}"
  }  

}

void execute_release() {

}
