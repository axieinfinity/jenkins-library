*Full example using keywords*
[source,groovy]
---
on_commit{
  github_status("connection1", "service-account", "running")
  continuous_integration()
  github_status("connection1", "service-account", "success")
}

on_pull_request to: develop, {
  github_status("connection2", "service-account", "pending")
  continuous_integration()
  github_status("connection2", "service-account", "running")
  deploy_to dev
  parallel "508 Testing": { accessibility_compliance_test() },
          "Functional Testing": { functional_test() },
          "Penetration Testing": { penetration_test() }
  deploy_to staging
  performance_test()
  github_status("connection2", "service-account", "success")
}

on_merge to: master, from: develop, {
  github_status("connection", "service-account2", "running")
  deploy_to prod
  smoke_test()
  github_status("connection", "service-account2", "success")
}
---