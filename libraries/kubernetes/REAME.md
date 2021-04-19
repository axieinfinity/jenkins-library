**Example**

```groovy
libraries {
  kubernetes
}

application_environments {
   cluster_dev {
        environment  = "dev"
        app_cred     = "/root/.gcloud/helm-secrets.json" 
        cluster_name = "axs-ops-gke"
        cluster_zone = "us-central1-a"
        project_id   = "axs-infra-ops-8686"
        release      = ""
    }
}
```
