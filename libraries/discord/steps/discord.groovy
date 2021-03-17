/* 
  author: namcxn
  date: Mar 02 00:00:00 +07 2021
 */

package libraries.discord

void call() {
  discordTo(
    title: "$JOB_NAME${env.CHANGE_TITLCHANGE_TITLEE ? ": $CHANGE_TITLE" : ''}",
    link: RUN_DISPLAY_URL,
    description: getResultDescription(),
    footer: 'Takes ' + currentBuild.durationString.replace(' and counting', ''),
    result: currentBuild.currentResult,
  )
}

void discordTo(Map parameters = [:]) {
  // if (config.discord_cred != null) {
  if (config.discord_cred != " ") {
    withCredentials([usernamePassword(
        credentialsId: parameters.credentialsId ?: config.discord_cred,
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
  } else {
    echo "Need config discord credential"
  }
}
