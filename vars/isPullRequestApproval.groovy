boolean call() {
    env.x_github_event == 'pull_request_review' && env.pr_review_state == 'approved'
}
