
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.customfields.manager.OptionsManager
import com.atlassian.jira.issue.fields.config.FieldConfig

// 1. Hämta fältet
def customFieldName = "Vad gäller ärendet?"
def customFieldManager = ComponentAccessor.customFieldManager
def optionsManager = ComponentAccessor.getComponent(OptionsManager)
def field = customFieldManager.getCustomFieldObjects().find { it.name == customFieldName }

// 2. Kontrollera om fältet finns
if (!field) {
    log.error "Fältet '$customFieldName' finns inte. Skapa det först."
    return
}

// 3. Hämta fältkonfiguration
def config = field.getRelevantConfig(ComponentAccessor.jiraApplicationContext.getGlobalIssueContext())
if (!config) {
    log.error "Ingen konfiguration hittades för fältet '$customFieldName'."
    return
}

// 4. Definiera alternativ (exempel)
def options = [
    [value: "Tak", position: 1],
    [value: "Ventilation", position: 2],
    [value: "El", position: 3],
    [value: "Kyl/FRY", position: 4]
]

// 5. Lägg till alternativ (om de inte redan finns)
options.each { opt ->
    def existingOption = optionsManager.getOptions(config).find { it.value == opt.value }
    if (existingOption) {
        log.warn "Alternativet '${opt.value}' finns redan (ID: ${existingOption.optionId})."
    } else {
        def newOption = optionsManager.createOption(config, null, opt.value, opt.position)
        log.info "Alternativet '${newOption.value}' (ID: ${newOption.optionId}) tillagt."
    }
}
