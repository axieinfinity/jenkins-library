void call(Map parameters = [:]) {
    withCredentials([string(
        credentialsId: parameters.credentialsId ?: 'webhook_token',
        variable: 'JENKINS_WEBHOOK_TOKEN',
    )]) {
        properties([
            pipelineTriggers([[
                $class: 'GenericTrigger',
                token: JENKINS_WEBHOOK_TOKEN,
                genericHeaderVariables: [
                    [key: 'X-GitHub-Event'],
                ],
                genericVariables: [
                    [key: 'action', value: '$.action'],
                    [key: 'org', value: '$.organization.login'],
                    [key: 'repo', value: '$.repository.name'],
                    // `issue_*` and `pr_*` keys are mutually exlusive.
                    [key: 'issue_id', value: '$.issue.number', defaultValue: ''],
                    [key: 'issue_state', value: '$.issue.state', defaultValue: ''],
                    [key: 'issue_comment', value: '$.comment.body', defaultValue: ''],
                    [key: 'pr_id', value: '$.pull_request.number', defaultValue: ''],
                    [key: 'pr_state', value: '$.pull_request.state', defaultValue: ''],
                    [key: 'pr_title', value: '$.pull_request.title'],
                    [key: 'pr_review_state', value: '$.review.state', defaultValue: ''],
                ],
                regexpFilterText: '$org/$repo/PR-$issue_id$pr_id:$issue_state$pr_state:$x_github_event:$action:$issue_comment$pr_review_state',
                regexpFilterExpression: "(?i)$JOB_NAME:open:(issue_comment:(created|edited):.*\\bretest this\\b.*|pull_request_review:submitted:approved)",
            ]]),
        ])
    }
}
