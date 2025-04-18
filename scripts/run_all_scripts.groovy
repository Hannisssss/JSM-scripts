def runScript(scriptPath) {
    try {
        def script = new File(scriptPath).text
        evaluate(script)  // Kör det externa skriptet
        log.info "Körde skriptet: ${scriptPath}"
    } catch (Exception e) {
        log.error "Fel vid körning av skriptet ${scriptPath}: ${e.message}"
    }
}

// Exempel på att köra ett skript från en lokal fil
def scriptDirectory = "/opt/jira/scripts"
runScript("${scriptDirectory}/deleteOldConfigurations.groovy")
runScript("${scriptDirectory}/createPortalGroups.groovy")
