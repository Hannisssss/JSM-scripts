def runScript(scriptPath) {
    try {
        def script = new File(scriptPath).text
        evaluate(script)
        log.info "Körde skriptet: ${scriptPath}"
    } catch (Exception e) {
        log.error "Fel vid körning av skriptet ${scriptPath}: ${e.message}"
    }
}

def scriptDirectory = "C:\\jira-home\\scripts"

runScript("${scriptDirectory}\\create_custom_fields.groovy")

