import hudson.model.Result
import org.jenkinsci.plugins.workflow.support.steps.build.RunWrapper

String call(RunWrapper currentBuild, boolean withPreviousResult = true) {
    String description = getDescription(currentBuild.currentResult, true)

    if (withPreviousResult) {
        RunWrapper previousBuild = currentBuild.previousBuild

        while (previousBuild && !previousBuild.result) {
            previousBuild = previousBuild.previousBuild
        }

        if (previousBuild && previousBuild.result) {
            String previousDescription = previousBuild.result != currentBuild.currentResult
                ? "changed from **${getDescription(previousBuild.result)}** in"
                : 'same as'

            String previousUrl = RUN_DISPLAY_URL.replaceAll('/[0-9]+/display/redirect', "/$previousBuild.number/display/redirect")

            description = "**$description** ($previousDescription [previous build](${previousUrl}))"
        }
    }

    description
}

String getDescription(Result result, boolean withEmoji = false) {
    String description = result.toString().toLowerCase().capitalize()

    if (withEmoji) {
        switch (result) {
            case Result.SUCCESS: return "$description 🎉"
            case Result.UNSTABLE: return "$description 🙃"
            case Result.FAILURE: return "$description 😢"
            case Result.ABORTED: return "$description 😔"
            default: return "$description 😐"
        }
    }

    description
}

String getDescription(String result, boolean withEmoji = false) {
    getDescription(Result.fromString(result), withEmoji)
}
