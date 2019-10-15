void call(Map parameters = [:]) {
    withCredentials([usernamePassword(
        credentialsId: parameters.credentialsId ?: 'discord_webhook',
        usernameVariable: 'DISCORD_WEBHOOK_ID',
        passwordVariable: 'DISCORD_WEBHOOK_TOKEN',
    )]) {
        discordSend(
            webhookURL: "https://discordapp.com/api/webhooks/$DISCORD_WEBHOOK_ID/$DISCORD_WEBHOOK_TOKEN",
            title: parameters.title,
            link: parameters.link,
            description: parameters.description,
            footer: parameters.footer,
            result: parameters.result,
        )
    }
}
