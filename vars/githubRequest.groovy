void call(Map parameters = [:]) {
    String apiRoot = (parameters.apiRoot ?: 'https://api.github.com').replaceAll('/+$', '')
    String endpoint = (parameters.endpoint ?: '').replaceAll('^/*', '/')
    List headers = parameters.headers ?: [];

    withCredentials([string(credentialsId: 'kotarobot', variable: 'GITHUB_TOKEN')]) {
        httpRequest(
            url: "$apiRoot$endpoint",
            httpMode: parameters.method ?: 'GET',
            customHeaders: [[name: 'Authorization', value: "Token $GITHUB_TOKEN"]] + headers,
            requestBody: parameters.body,
        )
    }
}
