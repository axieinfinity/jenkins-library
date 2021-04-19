## Example Configuration Snippet

```groovy
libraries{
  docker {
    build_strategy = "dockerfile"
    registry = "docker-registry.default.svc:5000"
    cred = "openshift-docker-registry"
    repo_path_prefix = "proj-images"
    remove_local_image = true
    build_args{
      GITHUB_TOKEN{
        type = "credential"
        id = "github_token"
      }
      SOME_VALUE = "some-inline-value-here"
    }
  }
}
```