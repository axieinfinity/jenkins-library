def call() {
    def a = equals(
        expected: 'pull_request_review',
        actual: env.x_github_event,
    )

    println a
    println a.class.simpleName

    a
}
