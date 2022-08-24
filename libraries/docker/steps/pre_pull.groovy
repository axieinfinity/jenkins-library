package libraries.docker

void call(dockerFilePath){
   sh """
      base_images=`cat $dockerFilePath | grep FROM | awk '{print \$2}'`
      for i in \$base_images
      do
        img_id=`docker images -q \$i`
        if [[ -z \$img_id ]]; then
          docker pull \$i
        fi
      done
    """
}
