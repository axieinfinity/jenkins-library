
package libraries.docker

void call(String _url = null, String _credentialId = null, def body){

  def (repository, cred) = get_registry_info()

  String protocol = config.registry_protocol ?: "https://"
  
  if ( repository =~ "(gcr.io|asia.gcr.io|eu.gcr.io|us.gcr.io)" ) {
    String url = _url ?: "${protocol}${repository}"
    String credentialId = _credentialId ?: "gcr:${cred}"
    docker.withRegistry(url, credentialId, body)
  } else {
    String url = _url ?: ""
    String credentialId = _credentialId ?: cred
    docker.withRegistry(url, credentialId, body)
  }

}
